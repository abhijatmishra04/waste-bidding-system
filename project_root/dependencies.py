import re
from pathlib import Path
from logging_config import logger
import networkx as nx

def parse_dependency_files(project_dir: str):
    dependencies = {}
    pom_files = list(Path(project_dir).rglob("pom.xml"))
    for pom_file in pom_files:
        with open(pom_file, 'r') as f:
            content = f.read()
            dependency_matches = re.findall(r'<dependency>.*?</dependency>', content, re.DOTALL)
            for dep in dependency_matches:
                group_id = re.search(r'<groupId>(.*?)</groupId>', dep)
                artifact_id = re.search(r'<artifactId>(.*?)</artifactId>', dep)
                version = re.search(r'<version>(.*?)</version>', dep)
                if group_id and artifact_id and version:
                    key = f"{group_id.group(1)}:{artifact_id.group(1)}"
                    dependencies[key] = version.group(1)
    logger.info("Dependency files parsed.")
    return dependencies

def detect_cyclic_dependencies(scan_summary, logger):
    dependency_graph = nx.DiGraph()
    for class_name, metadata in scan_summary.items():
        dependency_graph.add_node(class_name)
        for rel in metadata.get('relationships', []):
            if rel['relationship_type'] in ['method_call', 'data_access']:
                target = rel['target']
                dependency_graph.add_edge(class_name, target)

    cycles = list(nx.simple_cycles(dependency_graph))
    for cycle in cycles:
        logger.warning(f"Cyclic dependency detected: {' -> '.join(cycle)}")
        for class_in_cycle in cycle:
            scan_summary[class_in_cycle].setdefault('code_smells', []).append('Cyclic Dependency')

def check_dependency_versions(dependencies):
    outdated_dependencies = []
    for dep, version in dependencies.items():
        if 'SNAPSHOT' in version:
            outdated_dependencies.append({
                'dependency': dep,
                'version': version,
                'issue': 'Snapshot version used'
            })
    return outdated_dependencies
