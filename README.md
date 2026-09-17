# operations-copilot
Operations-copilot is an AI agent with access to operations tools of an order-shipment management system to help your operations team operate independently.

### Checkout the API docs along with examples of prompts supported by the operations-copilot
postman API docs: https://documenter.getpostman.com/view/56000409/2sBYB1NTZ2

### Build and run instructions
#### `.env `file required

    POSTGRES_DB_NAME=_db-name_  
    POSTGRES_DB_USER=<_db-user_  
    POSTGRES_DB_PASSWORD=_db-password_  
    JWT_SECRET=_jwt-secret-key_  
    GEMINI_API_KEY=_gemini-api-key_

#### Application Startup
    docker-compose up --build
