import yaml
from configparser import ConfigParser
from pathlib import Path
from logging_config import logger
from config import DEFAULT_CONFIG

def load_configuration(config_content):
    from scanner import config
    try:
        user_config = yaml.safe_load(config_content)
        config.update({**DEFAULT_CONFIG, **user_config})
        logger.info("Successfully loaded configuration.")
    except yaml.YAMLError as e:
        logger.error(f"Error parsing configuration file: {e}")
        config.update(DEFAULT_CONFIG)
    except Exception as e:
        logger.error(f"Error loading configuration: {e}")
        config.update(DEFAULT_CONFIG)

def apply_file_path_filters(java_files, config):
    include_filters = config['file_path_filters'].get('include', [])
    exclude_filters = config['file_path_filters'].get('exclude', [])

    filtered_files = []
    for java_file in java_files:
        file_str = str(java_file)
        if include_filters and not any(pattern in file_str for pattern in include_filters):
            continue
        if any(pattern in file_str for pattern in exclude_filters):
            continue
        filtered_files.append(java_file)

    logger.info(f"After applying filters, {len(filtered_files)} Java files will be scanned.")
    return filtered_files

def parse_configuration_files(project_dir: str):
    configurations = {}
    properties_files = list(Path(project_dir).rglob("application*.properties"))
    for prop_file in properties_files:
        config_parser = ConfigParser()
        with open(prop_file, 'r') as f:
            content = '[DEFAULT]\n' + f.read()
            config_parser.read_string(content)
            configurations.update(dict(config_parser['DEFAULT']))

    yml_files = list(Path(project_dir).rglob("application*.yml")) + list(Path(project_dir).rglob("application*.yaml"))
    for yml_file in yml_files:
        with open(yml_file, 'r') as f:
            yml_content = yaml.safe_load(f)
            if yml_content:
                configurations.update(yml_content)

    logger.info("Configuration files parsed.")
    return configurations
