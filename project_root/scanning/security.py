import javalang

def analyze_security(java_class):
    secured_methods = []
    if java_class.methods:
        for method in java_class.methods:
            for annotation in method.annotations:
                annotation_name = annotation.name if isinstance(annotation.name, str) else annotation.name.member
                if annotation_name in ["PreAuthorize", "PostAuthorize", "Secured", "RolesAllowed"]:
                    values = extract_annotation_value(annotation)
                    secured_methods.append({
                        'method': method.name,
                        'annotation': annotation_name,
                        'value': values
                    })
    if secured_methods:
        return {'secured_methods': secured_methods}
    else:
        return None

def detect_aspects(java_class):
    aspects = []
    class_annotations = [annotation.name if isinstance(annotation.name, str) else annotation.name.member for annotation in java_class.annotations]
    if "Aspect" in class_annotations:
        aspect_info = {
            'name': java_class.name,
            'advices': []
        }
        if java_class.methods:
            for method in java_class.methods:
                for annotation in method.annotations:
                    annotation_name = annotation.name if isinstance(annotation.name, str) else annotation.name.member
                    if annotation_name in ["Before", "After", "Around", "AfterReturning", "AfterThrowing", "Pointcut"]:
                        values = extract_annotation_value(annotation)
                        advice = {
                            'type': annotation_name,
                            'method': method.name,
                            'pointcut': values
                        }
                        aspect_info['advices'].append(advice)
        aspects.append(aspect_info)
    return aspects

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
