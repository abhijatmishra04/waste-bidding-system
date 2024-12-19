import os
from pathlib import Path
from concurrent.futures import ThreadPoolExecutor, as_completed
from logging_config import logger
from files import parse_configuration_files, apply_file_path_filters
from analysis import scan_batch
from  dependencies import detect_cyclic_dependencies,parse_dependency_files
from  report import generate_report
from config import DEFAULT_CONFIG

config = DEFAULT_CONFIG.copy()

def scan_project_async(project_dir: str, output_format: str = 'json', output_directory: str = None):
    """Scan the Java project asynchronously with filtering options."""
    scan_summary = {}
    metadata_cache = {}
    try:
        logger.info(f"Starting to scan project at {project_dir}")

        java_files = list(Path(project_dir).rglob("*.java"))

        if not java_files:
            logger.error("No Java files found in the directory.")
            return {"error": "No Java files found in the project."}

        logger.info(f"Found {len(java_files)} Java files to scan.")

        java_files = apply_file_path_filters(java_files, config)

        batch_size = config.get('batch_size', 50)
        total_files = len(java_files)
        processed_files = 0

        configurations = parse_configuration_files(project_dir)
        dependencies = parse_dependency_files(project_dir)

        with ThreadPoolExecutor(max_workers=os.cpu_count()) as executor:
            futures = []
            for batch in [java_files[i:i + batch_size] for i in range(0, len(java_files), batch_size)]:
                futures.append(executor.submit(scan_batch, batch, scan_summary, metadata_cache, configurations, dependencies, config))

            for future in as_completed(futures):
                try:
                    future.result()
                    processed_files += batch_size
                    progress = processed_files / total_files
                    logger.info(f"Progress: {min(progress, 1.0)*100}%")
                except Exception as exc:
                    logger.error(f"Batch generated an exception: {exc}")

        detect_cyclic_dependencies(scan_summary, logger)

        generate_report(output_format, project_dir, scan_summary, output_directory)
    except Exception as e:
        logger.error(f"Error during project scan: {e}")
        return {"error": f"Error during project scan: {e}"}

    return scan_summary
