# SpringbootOpenshitMysqlDemo
Demo project for Spring Boot MySql Openshift

## OpenAI portal assistant

The welcome page includes an assistant for questions about using the portal. It does not have access to student or college records. Assistant questions are sent to OpenAI; do not enter student information or other sensitive data.

Set an OpenAI API key in the environment before starting the application. For example, in PowerShell:

```powershell
$env:OPENAI_API_KEY = "your-api-key"
```

The application uses `gpt-4o-mini` by default. Override the model with `OPENAI_MODEL` or the Chat Completions endpoint with `OPENAI_API_URL` if needed. If no API key is configured, the rest of the application remains available and the assistant endpoint returns HTTP 503.
