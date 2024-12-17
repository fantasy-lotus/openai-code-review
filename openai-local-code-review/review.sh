# 配置输出目录
OUTPUT_DIR="./review_results/$(date +'%Y%m%d')"
OUTPUT_FILE="$OUTPUT_DIR/$(date +'%H%M%S')_review.md"
# 检查并创建输出目录
if [[ ! -d "$OUTPUT_DIR" ]]; then
    mkdir -p "$OUTPUT_DIR"
fi

# 调用审查工具
REVIEW=$(java -Dfile.encoding=UTF-8 -jar ./openai-code-review/openai-local-code-review-1.0.jar)

# 保存审查结果
echo -e "Code Review Results:\n" > "$OUTPUT_FILE"
echo "$REVIEW" >> "$OUTPUT_FILE"
echo "Review results saved to $OUTPUT_FILE"

