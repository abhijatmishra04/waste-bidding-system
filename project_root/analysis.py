from logging_config import logger
from javalang.parse import parse
from javalang.parser import JavaSyntaxError
from pathlib import Path

from scanning.utils import (
    extract_annotations, apply_annotation_filters, calculate_methods_complexity, detect_code_smells,
    extract_methods, extract_fields, detect_spring_component_type, detect_relationships,
    extract_config_properties, analyze_documentation
)
from dependencies import check_dependency_versions
from db import analyze_database_interactions, extract_database_entities
from messaging import detect_messaging_usage
from security import analyze_security, detect_aspects
from architecture import (
    detect_bounded_context, extract_api_endpoints, extract_domain_from_path,
    analyze_module_coupling, detect_external_api_calls, extract_spring_beans,
    extract_profiles, detect_inter_service_calls
)

def scan_batch(file_batch, scan_summary, metadata_cache, configurations, dependencies, config):
    for java_file in file_batch:
        scan_file(java_file, scan_summary, metadata_cache, configurations, dependencies, config)

def scan_file(java_file_path, scan_summary, metadata_cache, configurations, dependencies, config):
    try:
        with open(java_file_path, 'r', encoding='utf-8') as file:
            java_code = file.read()

        tree = parse(java_code)

        for node in tree.types:
            if node.__class__.__name__ == 'ClassDeclaration':
                scan_class(node, java_file_path, scan_summary, metadata_cache, configurations, dependencies, config, tree)
    except JavaSyntaxError as e:
        logger.error(f"Syntax error in {java_file_path}: {e}")
    except Exception as e:
        logger.error(f"Error parsing {java_file_path}: {e}")

def scan_class(java_class, java_file_path, scan_summary, metadata_cache, configurations, dependencies, config, tree):
    try:
        class_name = java_class.name
        last_modified = java_file_path.stat().st_mtime

        if class_name in metadata_cache and metadata_cache[class_name].get('last_modified', 0) >= last_modified:
            logger.info(f"Skipping cached class {class_name}")
            return

        class_annotations = extract_annotations(java_class)
        if not apply_annotation_filters(class_annotations, config):
            logger.info(f"Class {class_name} filtered out by annotation filter")
            return

        is_test_class = ('Test' in class_annotations) or ('test' in str(java_file_path).lower())

        bounded_context = detect_bounded_context(java_file_path)
        api_endpoints = extract_api_endpoints(java_class)
        domain = extract_domain_from_path(java_file_path)

        methods_complexity = calculate_methods_complexity(java_class)
        code_smells = detect_code_smells(java_class, methods_complexity, config, is_test_class)

        metadata = {
            'annotations': class_annotations,
            'component_type': detect_spring_component_type(java_class),
            'bounded_context': bounded_context,
            'api_endpoints': api_endpoints,
            'domain': domain,
            'methods_complexity': methods_complexity,
            'code_smells': code_smells,
            'layer': detect_layer(java_file_path),
            'file_path': str(java_file_path)
        }

        methods = extract_methods(java_class)
        if methods:
            metadata['methods'] = methods

        fields = extract_fields(java_class)
        if fields:
            metadata['fields'] = fields

        # Service injections
        detect_service_injections(java_class, metadata)

        relationships = detect_relationships(java_class, metadata)
        if relationships:
            metadata['relationships'] = relationships

        # Spring Beans
        if metadata['component_type'] in ["Configuration", "Component"]:
            beans = extract_spring_beans(java_class)
            if beans:
                metadata['spring_beans'] = beans

        # Security Analysis
        security_info = analyze_security(java_class)
        if security_info:
            metadata['security'] = security_info

        # AOP Analysis
        aspects = detect_aspects(java_class)
        if aspects:
            metadata['aspects'] = aspects

        # Profile and Conditional Beans
        profiles = extract_profiles(java_class)
        if profiles:
            metadata['profiles'] = profiles

        # Microservice Communication
        inter_service_calls = detect_inter_service_calls(java_class)
        if inter_service_calls:
            metadata['microservice_calls'] = inter_service_calls

        # DB Interactions
        db_interactions = analyze_database_interactions(java_class)
        if db_interactions:
            metadata['database_interactions'] = db_interactions

        # Database Entities
        database_entities = extract_database_entities(java_class)
        if database_entities:
            metadata['database_entities'] = database_entities

        # Messaging Usage
        messaging_usage = detect_messaging_usage(java_class)
        if messaging_usage:
            metadata['messaging_usage'] = messaging_usage

        # External API Calls
        external_api_calls = detect_external_api_calls(java_class)
        if external_api_calls:
            metadata['external_api_calls'] = external_api_calls

        # Config Properties
        config_properties = extract_config_properties(java_class)
        if config_properties:
            metadata['config_properties'] = config_properties

        # Module Coupling
        module_coupling = analyze_module_coupling(java_class)
        if module_coupling:
            metadata['module_coupling'] = module_coupling

        # Documentation
        documentation_coverage = analyze_documentation(java_class)
        if documentation_coverage:
            metadata['documentation_coverage'] = documentation_coverage

        # Outdated Dependencies
        outdated_dependencies = check_dependency_versions(dependencies)
        if outdated_dependencies:
            metadata['outdated_dependencies'] = outdated_dependencies

        metadata['last_modified'] = last_modified
        metadata_cache[class_name] = metadata
        scan_summary[class_name] = metadata

    except Exception as e:
        logger.error(f"Error scanning class {java_class.name}: {e}")

def detect_layer(java_file_path: Path) -> str:
    file_path_str = str(java_file_path).lower()
    if '/controller/' in file_path_str or file_path_str.endswith('controller.java'):
        return 'Controller Layer'
    elif '/service/' in file_path_str or file_path_str.endswith('service.java'):
        return 'Service Layer'
    elif '/repository/' in file_path_str or file_path_str.endswith('repository.java'):
        return 'Repository Layer'
    elif '/entity/' in file_path_str or file_path_str.endswith('entity.java'):
        return 'Entity Layer'
    else:
        return 'Unknown Layer'

def detect_service_injections(java_class, metadata):
    inter_service_calls = []
    # Field injection
    if java_class.fields:
        for field in java_class.fields:
            if field.annotations:
                for annotation in field.annotations:
                    name = annotation.name if isinstance(annotation.name, str) else annotation.name.member
                    if name in ["Autowired", "Inject", "Resource"]:
                        injected_service = field.type.name if field.type and field.type.name else 'Unknown'
                        inter_service_calls.append(injected_service)
    # Constructor injection
    if java_class.constructors:
        for constructor in java_class.constructors:
            if constructor.annotations:
                for annotation in constructor.annotations:
                    name = annotation.name if isinstance(annotation.name, str) else annotation.name.member
                    if name in ["Autowired", "Inject"]:
                        for param in constructor.parameters:
                            injected_service = param.type.name if param.type and param.type.name else 'Unknown'
                            inter_service_calls.append(injected_service)
    # Method injection
    if java_class.methods:
        for method in java_class.methods:
            if method.annotations:
                for annotation in method.annotations:
                    name = annotation.name if isinstance(annotation.name, str) else annotation.name.member
                    if name in ["Autowired", "Inject"]:
                        for param in method.parameters:
                            injected_service = param.type.name if param.type and param.type.name else 'Unknown'
                            inter_service_calls.append(injected_service)

    if inter_service_calls:
        metadata['inter_service_calls'] = inter_service_calls
