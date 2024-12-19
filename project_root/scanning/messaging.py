import javalang

def detect_messaging_usage(java_class):
    messaging_usage = []
    if java_class.fields:
        for field in java_class.fields:
            field_type = field.type.name if field.type else ''
            if field_type in ['KafkaTemplate', 'AmqpTemplate']:
                messaging_usage.append({
                    'type': field_type,
                    'usage': 'Producer'
                })
    if java_class.methods:
        for method in java_class.methods:
            if method.body:
                for path, node in method:
                    if isinstance(node, javalang.tree.MethodInvocation):
                        if node.member in ['send', 'convertAndSend']:
                            messaging_usage.append({
                                'method': method.name,
                                'call': node.member,
                                'usage': 'Messaging Send'
                            })
    return messaging_usage
