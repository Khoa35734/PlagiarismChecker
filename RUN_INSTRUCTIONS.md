# How to run this project (short)

1) Open PowerShell in the project root: `E:\btLTM\PlagiarismChecker`

2) Detect project type (look for one of these files):
   - Node: package.json
   - Python: requirements.txt, pyproject.toml, or manage.py
   - Java: pom.xml or build.gradle
   - .NET: .sln or *.csproj

3) Install prerequisites (if not installed):
   - Node.js (https://nodejs.org/)
   - Python 3.8+ (https://python.org/)
   - Java JDK (for Maven/Gradle)
   - .NET SDK (for dotnet projects)
   - Maven/Gradle if needed

4) Common run commands (choose based on detected type):
   - Node:
     - npm ci
     - npm start  (or `npm run dev`)
   - Python (virtualenv):
     - python -m venv .venv
     - .\.venv\Scripts\Activate
     - pip install -r requirements.txt
     - python main.py  (or `flask run` / `python manage.py runserver`)
   - Java (Maven):
     - mvn clean package
     - mvn spring-boot:run  (or `java -jar target/*.jar`)
   - .NET:
     - dotnet restore
     - dotnet run

5) Environment variables:
   - If the project uses a `.env` file, create it (copy `.env.example` if present).
   - Example: set in PowerShell:
     - $env:MY_KEY='value'
     - Or create `.env` in the project root.

6) Troubleshooting:
   - Check error messages in console.
   - Ensure ports are free (change port in config or env).
   - If missing files, open project root and list files: `Get-ChildItem -Force`

7) Automatic helper:
   - Run the provided script from PowerShell:
     - `Set-ExecutionPolicy -Scope Process -ExecutionPolicy Bypass`
     - `.\run_project.ps1`

