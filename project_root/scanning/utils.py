import javalang

def count_tokens(text):
    return len(text) / 4

def apply_annotation_filters(annotations, config):
    filters = config['annotation_filters']
    if not filters:
        return True
    return any(annotation in filters for annotation in annotations)

def extract_annotations(java_class):
    return [get_annotation_name(annotation) for annotation in java_class.annotations]

def get_annotation_name(annotation):
    if isinstance(annotation.name, str):
        return annotation.name
    elif hasattr(annotation.name, 'qualifier') and annotation.name.qualifier:
        return f"{annotation.name.qualifier}.{annotation.name.member}"
    else:
        return annotation.name.member

def extract_fields(java_class):
    fields = []
    if java_class.fields:
        for field in java_class.fields:
            field_info = {
                'names': [declarator.name for declarator in field.declarators],
                'type': field.type.name if field.type and isinstance(field.type.name, str) else 'Unknown',
                'annotations': [get_annotation_name(a) for a in field.annotations],
                'modifiers': list(field.modifiers)
            }
            fields.append(field_info)
    return fields

def extract_methods(java_class):
    methods = []
    if java_class.methods:
        for method in java_class.methods:
            try:
                method_info = {
                    'name': method.name,
                    'return_type': method.return_type.name if (method.return_type and isinstance(method.return_type.name, str)) else 'void',
                    'parameters': [{'name': param.name,
                                    'type': param.type.name if param.type and param.type.name else 'Unknown'}
                                   for param in method.parameters],
                    'annotations': [get_annotation_name(a) for a in method.annotations],
                    'modifiers': list(method.modifiers),
                    'body_length': len(method.body) if method.body else 0
                }
                methods.append(method_info)
            except Exception:
                pass
    return methods

def detect_spring_component_type(java_class):
    component_types = [
        "SpringBootApplication", "Configuration", "RestController", "Controller",
        "Service", "Repository", "Component"
    ]
    for annotation in java_class.annotations:
        annotation_name = get_annotation_name(annotation)
        if annotation_name in component_types:
            return annotation_name
    return "Unknown"

def detect_relationships(java_class, metadata):
    relationships = []
    relationships.extend(detect_inheritance(java_class))
    relationships.extend(detect_implementation(java_class))
    relationships.extend(detect_method_calls(java_class))
    relationships.extend(detect_data_flow(java_class))

    # Entity relationships
    if metadata.get('database_entities'):
        for entity in metadata['database_entities']:
            for rel in entity.get('relationships', []):
                relationships.append({
                    'type': 'entity_relationship',
                    'target': rel['target_entity'],
                    'relationship_type': rel['type']
                })

    meaningful_relationships = []
    for rel in relationships:
        target = rel.get('target', '')
        if target in ['this', 'super', java_class.name]:
            continue
        if len(target) <= 3:
            continue
        meaningful_relationships.append(rel)

    return meaningful_relationships

def detect_inheritance(java_class):
    rel = []
    if hasattr(java_class, 'extends') and java_class.extends:
        parent_class = java_class.extends.name if isinstance(java_class.extends.name, str) else java_class.extends.name.value
        rel.append({
            'type': 'inherits',
            'target': parent_class,
            'relationship_type': 'inheritance'
        })
    return rel

def detect_implementation(java_class):
    rel = []
    if java_class.implements:
        for interface in java_class.implements:
            interface_name = interface.name if isinstance(interface.name, str) else interface.name.value
            rel.append({
                'type': 'implements',
                'target': interface_name,
                'relationship_type': 'interface_implementation'
            })
    return rel

def detect_method_calls(java_class):
    relationships = []
    if java_class.methods:
        for method in java_class.methods:
            if method.body:
                for path, node in method:
                    if isinstance(node, javalang.tree.MethodInvocation):
                        target = node.qualifier if node.qualifier else 'this'
                        relationships.append({
                            'type': 'calls',
                            'target': target,
                            'relationship_type': 'method_call'
                        })
    return relationships

def detect_data_flow(java_class):
    relationships = []
    if java_class.methods:
        for method in java_class.methods:
            if method.body:
                for path, node in method:
                    if isinstance(node, javalang.tree.MemberReference):
                        relationships.append({
                            'type': 'data_flow',
                            'target': node.member,
                            'relationship_type': 'data_access'
                        })
    return relationships

def calculate_methods_complexity(java_class):
    methods_complexity = {}
    if java_class.methods:
        for method in java_class.methods:
            complexity = calculate_cyclomatic_complexity(method)
            methods_complexity[method.name] = complexity
    return methods_complexity

def calculate_cyclomatic_complexity(method):
    complexity = 1
    for path, node in method:
        if isinstance(node, (javalang.tree.IfStatement, javalang.tree.ForStatement,
                             javalang.tree.WhileStatement, javalang.tree.DoStatement,
                             javalang.tree.SwitchStatement, javalang.tree.TernaryExpression,
                             javalang.tree.CatchClause)):
            complexity += 1
    return complexity

def detect_code_smells(java_class, methods_complexity, config, is_test_class=False):
    code_smells = []
    methods_count = len(java_class.methods) if java_class.methods else 0
    fields_count = len(java_class.fields) if java_class.fields else 0

    if methods_count > config.get('class_size_threshold', 10) or fields_count > 10:
        code_smells.append('Large Class')

    method_length_threshold = config.get('method_length_threshold', 50)
    if java_class.methods:
        for method in java_class.methods:
            if method.body and len(method.body) > method_length_threshold:
                code_smells.append(f'Long Method: {method.name}')

    complexity_threshold = config.get('complexity_threshold', 10)
    for method_name, complexity in methods_complexity.items():
        if complexity > complexity_threshold:
            code_smells.append(f'Complex Method: {method_name} (Cyclomatic Complexity: {complexity})')

    annotations = extract_annotations(java_class)
    if 'RestController' in annotations and methods_count > 10:
        code_smells.append('Heavy Controller')

    if 'Service' in annotations and 'Repository' in annotations:
        code_smells.append('Ambiguous Component: Both @Service and @Repository present')

    if methods_count > 20 and fields_count > 20:
        code_smells.append('God Class')

    if methods_count == 0 and fields_count > 0:
        code_smells.append('Data Class')

    static_methods = sum(1 for m in java_class.methods if 'static' in m.modifiers) if java_class.methods else 0
    if java_class.methods and static_methods > methods_count / 2:
        code_smells.append('Excessive Static Methods')

    if is_test_class:
        code_smells = [smell for smell in code_smells if smell not in ['Large Class', 'God Class']]

    return code_smells

def extract_config_properties(java_class):
    config_properties = []
    if java_class.fields:
        for field in java_class.fields:
            annotations = [get_annotation_name(a) for a in field.annotations]
            if 'Value' in annotations:
                for annotation in field.annotations:
                    if get_annotation_name(annotation) == 'Value':
                        values = extract_annotation_value(annotation)
                        if values:
                            config_properties.append({
                                'field': field.declarators[0].name,
                                'property': values[0]
                            })
    return config_properties

def extract_annotation_value(annotation):
    values = []
    if annotation.element:
        if isinstance(annotation.element, javalang.tree.Literal):
            values.append(annotation.element.value.strip('"\''))

        elif isinstance(annotation.element, javalang.tree.ElementValuePair):
            if isinstance(annotation.element.value, javalang.tree.Literal):
                values.append(annotation.element.value.value.strip('"\''))

        elif isinstance(annotation.element, javalang.tree.ElementArrayValue):
            for element in annotation.element.values:
                if isinstance(element, javalang.tree.Literal):
                    values.append(element.value.strip('"\''))

        elif isinstance(annotation.element, list):
            for elem in annotation.element:
                if isinstance(elem, javalang.tree.ElementValuePair):
                    if isinstance(elem.value, javalang.tree.Literal):
                        values.append(elem.value.value.strip('"\''))

    return values

def analyze_documentation(java_class):
    documented_methods = 0
    total_methods = 0
    if java_class.methods:
        for method in java_class.methods:
            total_methods += 1

    coverage = (documented_methods / total_methods) * 100 if total_methods > 0 else 0
    return {
        'documented_methods': documented_methods,
        'total_methods': total_methods,
        'coverage': coverage
    }

def clean_metadata(metadata):
    fields_to_remove = ['last_modified', 'is_test_class']
    cleaned_metadata = {}

    # We'll keep file_path and make it relative later in report
    for key, value in metadata.items():
        if key in fields_to_remove:
            continue
        if value is None or value == '' or (isinstance(value, list) and not value) or (isinstance(value, dict) and not value):
            continue
        cleaned_metadata[key] = value

    return cleaned_metadata
