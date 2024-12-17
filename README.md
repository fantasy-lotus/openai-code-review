借助Github Actions和llm大模型自动化评审修改代码  
model: Pro/Qwen/Qwen2.5-Coder-7B-Instruct

## 接入项目使用教程:
1. 运行环境
- 请确保机器已经配置java运行环境, 可运行指令`java -version`确认
- 被接入项目需要接入Git
2. 复制初始化执行文件
```shell
# 设置下载目录
DOWNLOAD_DIR="./openai-code-review"
if [[ ! -d "$DOWNLOAD_DIR" ]]; then
    mkdir -p "$DOWNLOAD_DIR"
fi

# 添加路径到gitignore路径
echo "" >> .gitignore
echo "openai-code-review/" >> .gitignore
echo "review.sh" >> .gitignore

# 下载文件到指定目录
curl -LJO https://ghproxy.cn/https://github.com/fantasy-lotus/openai-code-review/releases/download/v1.0/review.sh
cd $DOWNLOAD_DIR
curl -LJO https://ghproxy.cn/https://github.com/fantasy-lotus/openai-code-review/releases/download/v1.0/openai-local-code-review-1.0.jar

# 自删除
rm "$0"
```
3. 粘贴至项目根目录下执行
4. 代码修改后, 在进行Git Add操作之前, 执行下载的review.sh进行代码审查
5. 审查结果会以markdown格式保存在review_results目录下, 以日期为文件夹名, 以时间为文件名
