import os

# 所有需要处理的文件
files = [
    r'c:\SharedLab\SharedLab_Test_Submission\同学乙_脚本与截图\接口测试\Device_Test.jmx',
    r'c:\SharedLab\SharedLab_Test_Submission\同学丙_脚本与截图\接口测试\Booking_Test.jmx',
    r'c:\SharedLab\SharedLab_Test_Submission\同学丁_脚本与截图\接口测试\Ticket_Test.jmx',
]

for file_path in files:
    if not os.path.exists(file_path):
        print(f"文件不存在: {file_path}")
        continue
        
    print(f"处理: {os.path.basename(file_path)}")
    
    # 读取文件
    with open(file_path, 'r', encoding='utf-8') as f:
        content = f.read()
    
    # 移除所有的 custom_message 行（包括重复的）
    lines = content.split('\n')
    filtered = []
    for line in lines:
        if 'name="Assertion.custom_message"' not in line:
            filtered.append(line)
    
    content = '\n'.join(filtered)
    
    # 现在在每个 test_field 后面添加 custom_message
    lines = content.split('\n')
    result = []
    
    for i, line in enumerate(lines):
        result.append(line)
        
        # 如果这行包含 test_field，且下一行不是custom_message
        if 'name="Assertion.test_field"' in line:
            result.append('              <stringProp name="Assertion.custom_message"></stringProp>')
    
    # 写回文件
    with open(file_path, 'w', encoding='utf-8', newline='') as f:
        f.write('\n'.join(result))
    
    print(f"完成: {os.path.basename(file_path)}")

print("\n所有文件处理完成！")
