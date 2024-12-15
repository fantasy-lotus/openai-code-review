curl --request POST \
  --url https://api.siliconflow.cn/v1/chat/completions \
  --header 'Authorization: Bearer sk-yqzobraqqlhwoilrnvckajfjkobwnauasdazieturopcvsez' \
  --header 'Content-Type: application/json' \
  --data '{
  "model": "THUDM/glm-4-9b-chat",
  "messages": [
    {
      "role": "user",
      "content": "什么是java面向对象"
    }
  ],
  "stream": false
}'