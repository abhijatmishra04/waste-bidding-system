import os
import yaml

ALLOWED_FILE_TYPES = ['zip']
MAX_UPLOAD_SIZE = 50 * 1024 * 1024  # 50 MB
GROQ_API_KEY = os.getenv("GROQ_API_KEY", "gsk_...")  # Use your key here
MAX_TOKENS = 5000000

DEFAULT_CONFIG = {
    'annotation_filters': [],
    'dependency_filters': {'include': [], 'exclude': []},
    'file_path_filters': {'include': [], 'exclude': []},
    'complexity_threshold': 10,
    'method_length_threshold': 50,
    'class_size_threshold': 10,
    'batch_size': 50,
    'output_file': 'scan_summary.json'
}
