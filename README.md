\# springai demo project

## Overview
This is a demo project with springai 1.0.3 to build question answering application.<br>
The application uses Qdrant as a vector store to store the embeddings of the documents and uses OpenAI's GPT-4 model to generate embeddings and answer questions.<br>

Features include:<br>
* Simple chat interface to ask questions
* Sentiment analysis example of a given text
* RAG (Retrieval Augmented Generation) example to answer questions based on the documents stored in Qdrant
* RAG (Retrieval Augmented Generation) example to answer questions based on the search results from Google Custom Search API
* Simple Bot Chat example.
* Simple Spring Tool example.


## Prerequisites
* Java 17+
* Docker
* Qdrant (Vector store)

## Qdrant setup
* install qdrant/qdrant:v1.3.1 on docker
    * docker run -p 6333:6333 -p 6334:6334 qdrant/qdrant
    * curl http://localhost:6333/collectionsù

