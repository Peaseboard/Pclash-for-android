import os
import re

def fix(file_path):
    with open(file_path, 'r') as f:
        content = f.read()

    is_app = '/app/' in file_path

    # 1. 删除之前脚本错误添加的 testOptions 行 (它们跑到了 android 块外面)
    content = re.sub(r'^\s*testOptions\.targetSdk.*$\n?', '', content, flags=re.MULTILINE)
    content = re.sub(r'^\s*lint\.targetSdk.*$\n?', '', content, flags=re.MULTILINE)

    if is_app:
        # 2. App 模块必须在 defaultConfig 中有 targetSdk
        if 'targetSdk = gTargetSdkVersion' not in content:
            # 自动插入到 defaultConfig { 内部
            content = re.sub(r'(defaultConfig\s*\{)', r'\1\n        targetSdk = gTargetSdkVersion', content, count=1)
    else:
        # 3. Library 模块删除 targetSdk 以消除警告 (这是最稳妥的做法)
        content = re.sub(r'^\s*targetSdk\s*=.*$\n?', '', content, flags=re.MULTILINE)

    with open(file_path, 'w') as f:
        f.write(content)
    print(f"✅ {os.path.basename(file_path)}")

for root, dirs, files in os.walk('.'):
    dirs[:] = [d for d in dirs if d not in ['.gradle', 'build', 'gradle']]
    for f in files:
        if f == 'build.gradle.kts':
            fix(os.path.join(root, f))

print("🎉 全部清理并修复完成！")
