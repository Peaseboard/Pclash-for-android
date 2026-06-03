import os
import re

def fix_build_gradle(file_path):
    with open(file_path, 'r') as f:
        lines = f.readlines()

    new_lines = []
    in_default_config = False
    g_target_found = False # 标记文件中是否存在这个变量

    for line in lines:
        stripped = line.strip()

        # 检查变量是否存在
        if 'gTargetSdkVersion' in stripped:
            g_target_found = True

        # 1. 清理由于重复替换导致的错误语法
        if 'testOptions.testOptions.targetSdk' in stripped:
            continue
        if 'lint.testOptions.targetSdk' in stripped:
            continue

        # 跟踪 defaultConfig 块
        if 'defaultConfig {' in stripped or 'defaultConfig{' in stripped:
            in_default_config = True
            new_lines.append(line)
            continue
        
        # 退出块
        if in_default_config and stripped == '}':
            in_default_config = False
            new_lines.append(line)
            continue

        # 2. 从 defaultConfig 中移除 targetSdk（消除警告）
        if in_default_config and stripped.startswith('targetSdk ='):
            continue
            
        new_lines.append(line)

    # 3. 如果存在变量且未配置，则在 dependencies 之前添加正确的配置
    content = "".join(new_lines)
    if g_target_found and 'testOptions.targetSdk' not in content:
        # 插入到 dependencies 之前
        fix = "    // Deprecated fix: moved out of defaultConfig\n    testOptions.targetSdk = gTargetSdkVersion\n    lint.targetSdk = gTargetSdkVersion\n\n"
        content = content.replace('dependencies {', fix + 'dependencies {')

    with open(file_path, 'w') as f:
        f.write(content)
    print(f"✅ Fixed: {file_path}")

# 遍历项目
for root, dirs, files in os.walk('.'):
    dirs[:] = [d for d in dirs if d not in ['.gradle', 'build', 'gradle']]
    for file in files:
        if file == 'build.gradle.kts':
            fix_build_gradle(os.path.join(root, file))

print("🎉 重复代码清理完成！请运行 ./gradlew clean")
