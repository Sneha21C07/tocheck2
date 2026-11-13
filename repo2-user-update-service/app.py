"""
Repo 2: User Update Service
Microservice with POST endpoint accepting any request body,
and updating the target `<param1>User-Detail.json` file created by Repo 1.
"""

import os
import json
from pathlib import Path
from typing import Optional
from flask import Flask, request, jsonify

app = Flask(__name__)

# Search paths for the target file
WORKSPACE_ROOT = Path(__file__).resolve().parent.parent
SHARED_STORAGE_DIR = Path(os.getenv("SHARED_STORAGE_DIR", str(WORKSPACE_ROOT / "shared_data")))
REPO1_DIR = WORKSPACE_ROOT / "repo1-user-query-service"


def locate_target_file(user_id: Optional[str] = None, filename: Optional[str] = None) -> Optional[Path]:
    """
    Finds the `<param1>User-Detail.json` file in shared_data, repo1, or workspace.
    If no specific user_id/filename provided, finds the most recently modified *User-Detail.json file.
    """
    candidates = []

    if filename:
        candidates.append(filename)
    if user_id:
        clean_id = "".join(c for c in user_id if c.isalnum() or c in ("-", "_"))
        candidates.append(f"{clean_id}User-Detail.json")
        candidates.append(f"{clean_id}-User-Detail.json")

    search_dirs = [SHARED_STORAGE_DIR, REPO1_DIR, Path(__file__).resolve().parent, WORKSPACE_ROOT]

    # 1. Exact candidate match
    for cand in candidates:
        for sdir in search_dirs:
            p = sdir / cand
            if p.exists() and p.is_file():
                return p

    # 2. Most recent *User-Detail.json if no specific match
    recent_files = []
    for sdir in search_dirs:
        if sdir.exists():
            for f in sdir.glob("*User-Detail.json"):
                recent_files.append((f.stat().st_mtime, f))

    if recent_files:
        recent_files.sort(key=lambda x: x[0], reverse=True)
        return recent_files[0][1]

    return None


@app.route("/api/users/update", methods=["POST"])
def update_user_body():
    """
    POST API accepting any JSON request body.
    Updates the target `<param1>User-Detail.json` with the incoming body payload.
    Example payload:
    {
      "userId": "USR101",
      "department": "Platform Architecture",
      "role": "Principal AI Architect",
      "status": "PROMOTED",
      "customNotes": "Updated by Repo 2 Service"
    }
    """
    if not request.is_json:
        return jsonify({"error": "Request body must be valid JSON"}), 400

    incoming_data = request.get_json()
    if not incoming_data or not isinstance(incoming_data, dict):
        return jsonify({"error": "JSON body cannot be empty and must be an object"}), 400

    # Extract user ID or filename hint from payload
    user_id = incoming_data.get("userId") or incoming_data.get("id") or incoming_data.get("param1")
    filename = incoming_data.get("targetFile") or incoming_data.get("filename")

    target_file = locate_target_file(user_id=user_id, filename=filename)

    if not target_file:
        # If file does not exist yet, create it using userId or DEFAULT
        uid = user_id or "USR-DEFAULT"
        clean_id = "".join(c for c in uid if c.isalnum() or c in ("-", "_"))
        target_file = SHARED_STORAGE_DIR / f"{clean_id}User-Detail.json"
        SHARED_STORAGE_DIR.mkdir(parents=True, exist_ok=True)
        existing_content = {}
    else:
        try:
            with open(target_file, "r", encoding="utf-8") as f:
                existing_content = json.load(f)
        except Exception:
            existing_content = {}

    # Merge and update existing data with incoming request body
    updated_content = dict(existing_content)
    updated_content.update(incoming_data)
    updated_content["_lastUpdatedBy"] = "repo2-user-update-service"

    # Write updated payload back to file
    with open(target_file, "w", encoding="utf-8") as f:
        json.dump(updated_content, f, indent=2)

    # Also sync to local copy if running inside repo2
    local_copy = Path(__file__).resolve().parent / target_file.name
    with open(local_copy, "w", encoding="utf-8") as f:
        json.dump(updated_content, f, indent=2)

    return jsonify({
        "message": "File updated successfully with request body",
        "updated_file": target_file.name,
        "file_path": str(target_file),
        "fields_updated": list(incoming_data.keys()),
        "updated_data": updated_content,
    }), 200


@app.route("/api/users/<user_id>/update", methods=["POST"])
def update_user_by_path(user_id: str):
    """
    POST API with path parameter specifying user_id:
    Example: POST /api/users/USR101/update
    Body:
    {
       "salary": 160000,
       "role": "Director of Engineering"
    }
    """
    if not request.is_json:
        return jsonify({"error": "Request body must be valid JSON"}), 400

    incoming_data = request.get_json()
    incoming_data["userId"] = user_id

    target_file = locate_target_file(user_id=user_id)
    if not target_file:
        clean_id = "".join(c for c in user_id if c.isalnum() or c in ("-", "_"))
        target_file = SHARED_STORAGE_DIR / f"{clean_id}User-Detail.json"
        SHARED_STORAGE_DIR.mkdir(parents=True, exist_ok=True)
        existing_content = {}
    else:
        try:
            with open(target_file, "r", encoding="utf-8") as f:
                existing_content = json.load(f)
        except Exception:
            existing_content = {}

    updated_content = dict(existing_content)
    updated_content.update(incoming_data)
    updated_content["_lastUpdatedBy"] = "repo2-user-update-service"

    with open(target_file, "w", encoding="utf-8") as f:
        json.dump(updated_content, f, indent=2)

    return jsonify({
        "message": f"File {target_file.name} updated successfully for {user_id}",
        "updated_file": target_file.name,
        "file_path": str(target_file),
        "updated_data": updated_content,
    }), 200


if __name__ == "__main__":
    port = int(os.getenv("PORT", "5002"))
    print(f"Starting Repo 2 (User Update Service) on port {port}...")
    app.run(host="0.0.0.0", port=port, debug=False)
