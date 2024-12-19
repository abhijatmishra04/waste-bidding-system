import javalang
from pathlib import Path

def detect_bounded_context(java_file_path: Path) -> str:
    file_path_str = str(java_file_path).lower()
    if "booking" in file_path_str:
        return "Booking Context"
    elif "passenger" in file_path_str:
        return "Passenger Context"
    elif "flight" in file_path_str:
        return "Flight Context"
    else:
        return "Unknown Context"

def extract_api_endpoints(java_class):
    api_endpoints = []
    class_mappings = []
    for annotation in java_class.annotations:
        annotation_name = annotation.name if isinstance(annotation.name, str) else annotation.name.member
        if annotation_name == "RequestMapping":
            values = extract_annotation_value(annotation)
            class_mappings.extend(values)

    if not class_mappings:
        class_mappings = ['/']

    if java_class.methods:
        for method in java_class.methods:
            method_mappings = []
            http_methods = []
            for annotation in method.annotations:
                a_name = annotation.name if isinstance(annotation.name, str) else annotation.name.member
                if a_name in {"RequestMapping", "GetMapping", "PostMapping", "PutMapping", "DeleteMapping", "PatchMapping"}:
                    values = extract_annotation_value(annotation)
                    if values:
                        method_mappings.extend(values)
                    else:
                        method_mappings.append('/')
                    http_methods.append(a_name.replace('Mapping', '').upper() if 'Mapping' in a_name else a_name.upper())

            if not method_mappings:
                method_mappings = ['/']
            for class_mapping in class_mappings:
                for method_mapping in method_mappings:
                    full_mapping = (class_mapping.rstrip('/') + '/' + method_mapping.lstrip('/')).replace('//', '/')
                    endpoint_info = f"{','.join(http_methods)} {full_mapping}"
                    api_endpoints.append(endpoint_info)
    return api_endpoints

def extract_domain_from_path(java_file_path: Path) -> str:
    path_parts = str(java_file_path).split('/')
    if "booking" in [p.lower() for p in path_parts]:
        return "Booking Domain"
    elif "flight" in [p.lower() for p in path_parts]:
        return "Flight Domain"
    elif "passenger" in [p.lower() for p in path_parts]:
        return "Passenger Domain"
    else:
        return "Unknown Domain"

def analyze_module_coupling(java_class):
    coupling = []
    if java_class.methods:
        for method in java_class.methods:
            if method.body:
                for path, node in method:
                    if isinstance(node, javalang.tree.MethodInvocation):
                        if node.qualifier and '.' in node.qualifier:
                            package_name = '.'.join(node.qualifier.split('.')[:-1])
                            coupling.append({
                                'method': method.name,
                                'coupled_module': package_name
                            })
    return coupling

def detect_external_api_calls(java_class):
    external_api_calls = []
    # Fields with FeignClient
    if java_class.fields:
        for field in java_class.fields:
            annotations = [a.name if isinstance(a.name, str) else a.name.member for a in field.annotations]
            field_type = field.type.name if field.type else ''
            if 'FeignClient' in annotations:
                external_api_calls.append({
                    'field': field.declarators[0].name,
                    'type': field_type,
                    'usage': 'Feign Client'
                })
    # Method calls with restTemplate or webClient
    if java_class.methods:
        for method in java_class.methods:
            if method.body:
                for path, node in method:
                    if isinstance(node, javalang.tree.MethodInvocation):
                        if node.qualifier and node.qualifier in ['restTemplate', 'webClient']:
                            target = node.arguments[0].value if node.arguments else 'Unknown'
                            external_api_calls.append({
                                'method': method.name,
                                'call': node.member,
                                'usage': 'External API Call',
                                'target': target
                            })
    return external_api_calls

def extract_spring_beans(java_class):
    beans = []
    if java_class.methods:
        for method in java_class.methods:
            for annotation in method.annotations:
                a_name = annotation.name if isinstance(annotation.name, str) else annotation.name.member
                if a_name == "Bean":
                    return_type = method.return_type.name if (method.return_type and isinstance(method.return_type.name, str)) else 'Unknown'
                    beans.append({
                        'name': method.name,
                        'return_type': return_type
                    })
    return beans

def extract_profiles(java_class):
    profiles = []
    for annotation in java_class.annotations:
        a_name = annotation.name if isinstance(annotation.name, str) else annotation.name.member
        if a_name in ["Profile", "ConditionalOnProperty", "Conditional"]:
            values = extract_annotation_value(annotation)
            profiles.extend(values)
    return profiles

def detect_inter_service_calls(java_class):
    inter_service_calls = []
    
    return inter_service_calls

def extract_annotation_value(annotation):
    values = []
    if annotation.element:
        import javalang
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
