import os
import json
import traceback
from logging_config import logger
from  project_root. scanning.utils import clean_metadata

def generate_report(output_format='json', project_path=None, scan_summary=None, output_directory=None):
    logger.info("Generating scan reports.")

    if output_format == 'json':
        if output_directory is None:
            output_directory = project_path
        output_directory = os.path.abspath(output_directory)
        logger.info(f"Output directory is set to: {output_directory}")

        test_file_path = os.path.join(output_directory, 'test_write.txt')
        try:
            with open(test_file_path, 'w') as f:
                f.write('Test write to verify permissions.')
            os.remove(test_file_path)
            logger.info("Write test successful.")
        except Exception as e:
            logger.error(f"Cannot write to the specified output directory: {e}")
            logger.error(f"Traceback: {traceback.format_exc()}")
            return

        code_analysis_file = os.path.join(output_directory, 'code_analysis.json')
        data_analysis_file = os.path.join(output_directory, 'data_analysis.json')
        combined_analysis_file = os.path.join(output_directory, 'combined_analysis.json')

        try:
            code_analysis_data = {}
            data_analysis_data = {}
            combined_analysis_data = {}

            for class_name, metadata in scan_summary.items():
                code_analysis_metadata = {k: v for k, v in metadata.items() if k in [
                    'annotations', 'methods', 'fields', 'component_type', 'layer',
                    'methods_complexity', 'code_smells', 'relationships'
                ]}

                data_analysis_metadata = {k: v for k, v in metadata.items() if k in [
                    'database_entities', 'config_properties', 'external_api_calls',
                    'messaging_usage', 'api_endpoints', 'bounded_context', 'domain'
                ]}

                code_analysis_metadata = clean_metadata(code_analysis_metadata)
                data_analysis_metadata = clean_metadata(data_analysis_metadata)

                # Make file_path relative in code and combined metadata if exists
                combined_metadata = clean_metadata(metadata.copy())
                if 'file_path' in metadata:
                    rel_path = os.path.relpath(metadata['file_path'], project_path)
                    code_analysis_metadata['file_path'] = rel_path
                    combined_metadata['file_path'] = rel_path

                code_analysis_data[class_name] = code_analysis_metadata
                if data_analysis_metadata:
                    data_analysis_data[class_name] = data_analysis_metadata
                combined_analysis_data[class_name] = combined_metadata

            with open(code_analysis_file, 'w') as f:
                json.dump(code_analysis_data, f, indent=4)
            logger.info(f"Code analysis JSON written to {code_analysis_file}")

            with open(data_analysis_file, 'w') as f:
                json.dump(data_analysis_data, f, indent=4)
            logger.info(f"Data analysis JSON written to {data_analysis_file}")

            with open(combined_analysis_file, 'w') as f:
                json.dump(combined_analysis_data, f, indent=4)
            logger.info(f"Combined analysis JSON written to {combined_analysis_file}")

        except Exception as e:
            logger.error(f"Error writing JSON reports: {e}")
            logger.error(f"Traceback: {traceback.format_exc()}")
    else:
        logger.error(f"Unsupported output format or missing project path: {output_format}")

def generate_architecture_prompt(scan_summary_data):
    prompt_lines = []
    total_token_count = 0
    MAX_TOKENS = 5000000

    prompt_lines.append("### Enhanced Java Spring Boot Project Architecture Overview ###\n\n")
    total_token_count += len(prompt_lines[-1]) / 4

    for class_name, metadata in scan_summary_data.items():
        if total_token_count >= MAX_TOKENS:
            prompt_lines.append("\n...Output truncated to stay within token limit...\n")
            break

        prompt_lines.append(f"Class: {class_name}\n")
        total_token_count += len(prompt_lines[-1]) / 4

        prompt_lines.append(f"Component Type: {metadata.get('component_type', 'Unknown')}\n")
        total_token_count += len(prompt_lines[-1]) / 4

        prompt_lines.append(f"Layer: {metadata.get('layer', 'Unknown')}\n")
        total_token_count += len(prompt_lines[-1]) / 4

        prompt_lines.append(f"Bounded Context: {metadata.get('bounded_context', 'Unknown')}\n")
        total_token_count += len(prompt_lines[-1]) / 4

        if metadata.get('annotations'):
            annotations = ', '.join(metadata['annotations'][:3])
            prompt_lines.append(f"Annotations: {annotations}\n")
        else:
            prompt_lines.append("Annotations: None\n")
        total_token_count += len(prompt_lines[-1]) / 4

        if metadata.get('code_smells'):
            code_smells = ', '.join(metadata['code_smells'])
            prompt_lines.append(f"Code Smells: {code_smells}\n")
        else:
            prompt_lines.append("Code Smells: None\n")
        total_token_count += len(prompt_lines[-1]) / 4

        if metadata.get('relationships'):
            prompt_lines.append("Relationships:\n")
            for rel in metadata['relationships']:
                prompt_lines.append(f"  - {rel['type']} {rel['target']} ({rel['relationship_type']})\n")
                total_token_count += len(prompt_lines[-1]) / 4

        prompt_lines.append("\n")
        total_token_count += len(prompt_lines[-1]) / 4

    prompt_text = ''.join(prompt_lines)
    return prompt_text
