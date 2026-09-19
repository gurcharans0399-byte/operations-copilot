# Operations Copilot Design

### Microservices:
* Auth-service -> Handling user authentication using JWT
* Order-service -> Handling Order,Delivery,Payment & Refund entities and corresponding business logic
* Query-service -> Handling queries made to the LLM agent and response provided by it.

### Queries supported:
    - Read queries: Status / DB record fetching & reasoning
    - Write queries: Only Initiate Refund supported for now


### Write Query User Flow:
    Overview:
        (1) Propose Refund - LLM Agent is only involved untill step (1) for creating a ProposedAction pointing to a ProposedRefund, which is different from an actual refund
        (2) View proposed action
        (3) Mandatorily human-verify response & update(optional) - Human verification is made mandatory before actual DB update
        (4) Confirm for update in database - Refund persisted in the DB

Example of the above user-flow can be seen in the API docs

### APIs (user facing)
| Description | AP|
|-------------|---|
|LLM query| POST /opcopilot/query|
|Close chat session| POST /opcopilot/close|
|Login| POST /auth/login|
|View proposed action| GET  /user/proposed-action/{proposedActionId}|
|Update proposed action| PUT  /user/proposed-action/{proposedActionId}|
|Confirm manual verification of proposed action| PUT  /user/proposed-action/{proposedActionId}/verify|
|Confirm persistence of proposed action to DB| POST /user/proposed-action/{proposedActionId}/confirm|


### API Response:
```
POST - localhost:8082/opcopilot/query
Request:
{
    "query": "Give me a full summary of order #73062f17-3a08-4d05-b3ce-98054c168c7e, "
}

Response:
{
    "response": "Order Summary for #73062f17-3a08-4d05-b3ce-98054c168c7e: <br>Order Details: Order ID: 73062f17-3a08-4d05-b3ce-98054c168c7e, Status: DELIVERED, Order Date: 2024-05-15T10:00:00Z, Customer ID: CUST-98765, Total Amount: $150.00. <br>Payment Details: Transaction ID: PAY-12345, Status: COMPLETED, Amount: $150.00, Payment Date: 2024-05-15T10:05:00Z. <br>Delivery Logs: <br><table><tr><th>Status</th><th>Timestamp</th></tr><tr><td>ORDER_RECEIVED</td><td>2024-05-15T10:00:00Z</td></tr><tr><td>PREPARING_SHIPMENT</td><td>2024-05-15T11:00:00Z</td></tr><tr><td>SHIPPED</td><td>2024-05-16T09:00:00Z</td></tr><tr><td>OUT_FOR_DELIVERY</td><td>2024-05-17T08:00:00Z</td></tr><tr><td>DELIVERED</td><td>2024-05-17T14:00:00Z</td></tr></table>",
    "manualActionRequired": false,
    "promptToken": 1053,
    "responseToken": 386,
    "conversationTokens": 5427
}
```
**response**: LLM response in HTML format   
**manualActionRequired**: boolean to hint at shifting control to user   
**promptToken**: Current prompt tokens   
**responseToken**: Current response tokens   
**conversationTokens**: Tokens used in the current conversation   

### Operations constrainsts for the agent provided via SystemMessage:
    You are an AI assistant for an operations team.
    Your job is to help operations employees answer questions about customers, orders, payments and deliveries.
    Global Rules:
    - Answer clearly and concisely.Do not invent operational information.
    - If information is not available, say that you do not have enough information to answer.
    * User flows:
    - READ query -> A read query could use standalone tools to fetch required information from the DB or reason 
        or make an inference based on the results of multiple tools.
    - WRITE query -> Create a ProposedAction and ProposedRefund object via the provided tools. 
        Do no update attributes like consentor, consentDate, manuallyVerified or update any ProposedAction state on your own.
        For UPDATE/CREATE/DATE flow provide the response of the tools as-is to the user. And wait for user confirmation.
    - Try to answer the query in natural language along with the relevant DB records in the response.
    - Do not invent information in any case
    - No need to answer unrelated queries or ambiguous queries or queries without the required details.
    - User HTML in your text responses and any DB data you need to include in your response should be formatted in a table format for better readability.
    - Do not include newline character in your response, use html instead.
