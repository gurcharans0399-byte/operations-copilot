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

#### Startup Logs:
Seed order Ids are logged to the console at startup as:<br>
```
order-service    | All Orders: 
order-service    | Id: 6bf769f1-f614-49c4-8675-285ff2b7fe78 status: DELIVERED
order-service    | Id: 909e7b2c-1342-4351-a3a6-d875eb536b89 status: PAYMENT_ERROR
order-service    | Id: 7d5e1751-aadc-4b46-a3b2-1e79bb67dfe9 status: SHIPPED
```

#### Examples of supported queries:

- What is the issue with order #orderId, The user isn't able to complete payment.
- Give me a full summary of the order #orderId.
- Can you check the payment and delivery status of the order #orderId. The user says payment is completed but he hasn't received the delivery for quite sometime.
- Initiate a refund for the order #orderId.

The query response is provided in HTML format.
