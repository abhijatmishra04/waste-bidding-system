import os
import tempfile
import zipfile
import traceback
from pathlib import Path
from dotenv import load_dotenv
from fastapi import FastAPI, UploadFile, File, Form, HTTPException
from fastapi.responses import JSONResponse
import sys
import os
sys.path.append(os.path.abspath(os.path.join(os.path.dirname(__file__), "..")))

from config import ALLOWED_FILE_TYPES, MAX_UPLOAD_SIZE, GROQ_API_KEY, DEFAULT_CONFIG, MAX_TOKENS
from logging_config import logger
from scanning.scanner import scan_project_async
from scanning.report import generate_architecture_prompt
from scanning.files import load_configuration

load_dotenv()

app = FastAPI()

@app.post("/scan_project")
async def scan_project_endpoint(
    project_zip: UploadFile = File(...),
    config_file: UploadFile = File(None),
    complexity_threshold: int = Form(10),
    method_length_threshold: int = Form(50),
    class_size_threshold: int = Form(10),
    output_format: str = Form('json'),
    output_directory: str = Form(None)
):
    if not project_zip:
        return JSONResponse(status_code=400, content={"error": "Please provide a ZIP file of the Java project."})

    # Save the uploaded project ZIP file
    with tempfile.TemporaryDirectory() as temp_dir:
        project_path = temp_dir

        # Handle ZIP file upload
        if project_zip:
            if project_zip.content_type != 'application/zip':
                return JSONResponse(status_code=400, content={"error": "Invalid file type. Please upload a ZIP file."})

            project_zip_path = os.path.join(temp_dir, project_zip.filename)
            try:
                with open(project_zip_path, 'wb') as f:
                    while True:
                        contents = await project_zip.read(1024)
                        if not contents:
                            break
                        f.write(contents)
            except Exception as e:
                logger.error(f"Error saving uploaded file: {e}")
                return JSONResponse(status_code=500, content={"error": "Failed to save uploaded file."})

            # Check size
            file_size = os.path.getsize(project_zip_path)
            if file_size > MAX_UPLOAD_SIZE:
                return JSONResponse(status_code=400, content={"error": f"File size exceeds {MAX_UPLOAD_SIZE / (1024 * 1024)} MB."})

            # Extract the uploaded ZIP file
            try:
                with zipfile.ZipFile(project_zip_path, 'r') as zip_ref:
                    zip_ref.extractall(temp_dir)
                logger.info("Project uploaded and extracted successfully!")
            except zipfile.BadZipFile:
                logger.error("Invalid ZIP file.")
                return JSONResponse(status_code=400, content={"error": "Invalid ZIP file."})

        # Load configuration if provided
        from scanning.scanner import config
        if config_file:
            config_file_path = os.path.join(temp_dir, config_file.filename)
            try:
                with open(config_file_path, 'wb') as f:
                    while True:
                        contents = await config_file.read(1024)
                        if not contents:
                            break
                        f.write(contents)
            except Exception as e:
                logger.error(f"Error saving configuration file: {e}")
                return JSONResponse(status_code=500, content={"error": "Failed to save configuration file."})

            # Load configuration from the saved file
            with open(config_file_path, 'r') as f:
                config_content = f.read()
            load_configuration(config_content)
        else:
            logger.warning("Using default configuration parameters.")
            config.update({
                'complexity_threshold': complexity_threshold,
                'method_length_threshold': method_length_threshold,
                'class_size_threshold': class_size_threshold
            })

        # Start scanning the project
        scan_summary = scan_project_async(project_path, output_format, output_directory)

        # If there was an error, return it
        if "error" in scan_summary:
            return JSONResponse(status_code=400, content=scan_summary)

        # Generate architecture prompt
        architecture_prompt = generate_architecture_prompt(scan_summary)


        return JSONResponse(content={
            "scan_summary": "Scan completed successfully. Check the generated JSON files.",
            "architecture_prompt": architecture_prompt
        })
