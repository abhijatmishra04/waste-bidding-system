import javalang

def analyze_database_interactions(java_class):
    db_interactions = []
    implements_interfaces = [impl.name for impl in java_class.implements] if java_class.implements else []
    annotations = [a.name if isinstance(a.name, str) else a.name.member for a in java_class.annotations]
    if 'Entity' in annotations or 'Repository' in annotations or 'JpaRepository' in implements_interfaces:
        if java_class.methods:
            for method in java_class.methods:
                method_info = {
                    'name': method.name,
                    'query': extract_query_from_method(method)
                }
                db_interactions.append(method_info)
    return db_interactions

def extract_query_from_method(method):
    for annotation in method.annotations:
        annotation_name = annotation.name if isinstance(annotation.name, str) else annotation.name.member
        if annotation_name == 'Query':
            values = extract_annotation_value(annotation)
            if values:
                return values[0]
    return f"Derived query based on method name: {method.name}"

def extract_database_entities(java_class):
    entities = []
    annotations = [annotation.name if isinstance(annotation.name, str) else annotation.name.member for annotation in java_class.annotations]
    if 'Entity' in annotations:
        entity_info = {
            'table_name': None,
            'fields': [],
            'relationships': []
        }
        for annotation in java_class.annotations:
            annotation_name = annotation.name if isinstance(annotation.name, str) else annotation.name.member
            if annotation_name == 'Table':
                values = extract_annotation_value(annotation)
                if values:
                    entity_info['table_name'] = values[0]

        if java_class.fields:
            for field in java_class.fields:
                field_annotations = [a.name if isinstance(a.name, str) else a.name.member for a in field.annotations]
                column_name = None
                for annotation in field.annotations:
                    a_name = annotation.name if isinstance(annotation.name, str) else annotation.name.member
                    if a_name == 'Column':
                        c_values = extract_annotation_value(annotation)
                        if c_values:
                            column_name = c_values[0]
                field_info = {
                    'name': field.declarators[0].name if field.declarators else 'Unknown',
                    'type': field.type.name if field.type else 'Unknown',
                    'column_name': column_name,
                    'annotations': field_annotations
                }
                entity_info['fields'].append(field_info)
                relationship = detect_entity_relationships(field)
                if relationship:
                    entity_info['relationships'].append(relationship)
        entities.append(entity_info)
    return entities

def detect_entity_relationships(field):
    relationship_types = ['OneToMany', 'ManyToOne', 'ManyToMany', 'OneToOne']
    for annotation in field.annotations:
        a_name = annotation.name if isinstance(annotation.name, str) else annotation.name.member
        if a_name in relationship_types:
            target_entity = None
            mapped_by = None
            if annotation.element:
                if isinstance(annotation.element, javalang.tree.ElementValuePair):
                    if annotation.element.name == 'mappedBy':
                        mapped_by = annotation.element.value.value.strip('"\'')
                    elif annotation.element.name == 'targetEntity':
                        target_entity = annotation.element.value.value.strip('"\'')
                elif isinstance(annotation.element, list):
                    for elem in annotation.element:
                        if elem.name == 'mappedBy':
                            mapped_by = elem.value.value.strip('"\'')
                        elif elem.name == 'targetEntity':
                            target_entity = elem.value.value.strip('"\'')
            return {
                'type': a_name,
                'field': field.declarators[0].name if field.declarators else 'Unknown',
                'target_entity': target_entity,
                'mapped_by': mapped_by
            }
    return None

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
