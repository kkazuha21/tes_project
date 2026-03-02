import os
import glob

# 查找所有JMX文件
jmx_files = glob.glob(r'c:\SharedLab\SharedLab_Test_Submission\**\*_Test.jmx', recursive=True)

for file_path in jmx_files:
    print(f"处理: {os.path.basename(file_path)}")
    
    # 读取文件
    with open(file_path, 'r', encoding='utf-8') as f:
        lines = f.readlines()
    
    # 删除重复的 custom_message
    result = []
    prev_was_custom_msg = False
    
    for line in lines:
        is_custom_msg = 'name="Assertion.custom_message"' in line
        
        # 如果当前行是 custom_message 且前一行也是，跳过
        if is_custom_msg and prev_was_custom_msg:
            print(f"  跳过重复行: {line.strip()}")
            continue
        
        result.append(line)
        prev_was_custom_msg = is_custom_msg
    
    # 写回文件
    with open(file_path, 'w', encoding='utf-8', newline='') as f:
        f.writelines(result)
    
    print(f"完成: {os.path.basename(file_path)}")

print("\n所有文件处理完成！")
