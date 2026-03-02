import os
import glob
import re

# 查找所有JMX文件
jmx_files = glob.glob(r'c:\SharedLab\SharedLab_Test_Submission\**\*_Test.jmx', recursive=True)

for file_path in jmx_files:
    print(f"处理: {os.path.basename(file_path)}")
    
    # 读取文件
    with open(file_path, 'r', encoding='utf-8') as f:
        lines = f.readlines()
    
    # 处理每一行
    result = []
    i = 0
    while i < len(lines):
        line = lines[i]
        result.append(line)
        
        # 检查是否是 test_field 行
        if 'name="Assertion.test_field"' in line:
            # 检查下一行是否是 assume_success（即缺少 custom_message）
            if i + 1 < len(lines) and 'name="Assertion.assume_success"' in lines[i + 1]:
                # 插入 custom_message
                indent = '              '  # 14个空格
                result.append(f'{indent}<stringProp name="Assertion.custom_message"></stringProp>\n')
        
        i += 1
    
    # 写回文件
    with open(file_path, 'w', encoding='utf-8', newline='') as f:
        f.writelines(result)
    
    print(f"完成: {os.path.basename(file_path)}")

print("\n所有文件处理完成！")
