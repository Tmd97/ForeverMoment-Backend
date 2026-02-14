import os

def replace_in_file(filepath):
    try:
        with open(filepath, 'r', encoding='utf-8') as f:
            content = f.read()
        
        new_content = content.replace('com.example.moment_forever', 'com.forvmom')
        
        if content != new_content:
            with open(filepath, 'w', encoding='utf-8') as f:
                f.write(new_content)
            print(f"Updated: {filepath}")
    except Exception as e:
        print(f"Error processing {filepath}: {e}")

def main():
    root_dir = r"c:\Project_APP\MomentForeverApp"
    print(f"Scanning {root_dir}...")
    
    for dirpath, dirnames, filenames in os.walk(root_dir):
        for filename in filenames:
            if filename.endswith(".java") or filename.endswith(".xml") or filename.endswith(".yml"):
                replace_in_file(os.path.join(dirpath, filename))

if __name__ == "__main__":
    main()
