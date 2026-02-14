import os

def annotate_controller(filepath, tag_name, description):
    try:
        with open(filepath, 'r', encoding='utf-8') as f:
            lines = f.readlines()
        
        new_lines = []
        imports_added = False
        class_annotated = False
        
        for line in lines:
            if line.startswith("package ") and not imports_added:
                new_lines.append(line)
                new_lines.append("import io.swagger.v3.oas.annotations.Operation;\n")
                new_lines.append("import io.swagger.v3.oas.annotations.tags.Tag;\n")
                imports_added = True
                continue
            
            if (line.strip().startswith("public class") or line.strip().startswith("@RestController")) and not class_annotated and "@Tag" not in "".join(lines):
                 if line.strip().startswith("@RestController"):
                     new_lines.append(f'@Tag(name = "{tag_name}", description = "{description}")\n')
                     class_annotated = True
                 elif line.strip().startswith("public class"):
                     # case where RestController might be on previous line or not there (unlikely for controller)
                     if not class_annotated: 
                        new_lines.append(f'@Tag(name = "{tag_name}", description = "{description}")\n')
                        class_annotated = True
            
            # Simple heuristic to add @Operation to public methods with mapping annotations
            if ("@GetMapping" in line or "@PostMapping" in line or "@PutMapping" in line or "@DeleteMapping" in line) and "@Operation" not in line:
                 # This is a very basic heuristic and might need manual refinement
                 pass 

            new_lines.append(line)
            
        with open(filepath, 'w', encoding='utf-8') as f:
            f.writelines(new_lines)
        print(f"Annotated: {filepath}")

    except Exception as e:
         print(f"Error processing {filepath}: {e}")

# This script is a template. I will perform edits manually or via more specific tools to ensure quality.
