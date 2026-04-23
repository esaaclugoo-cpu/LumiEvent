import os

templates_path = r'C:\Users\esaac\Documents\ILERNA\Programacion\PROYECTO TRANSVERSAL\NovaTicket\src\main\resources\templates'

# The files were double-encoded: UTF-8 bytes interpreted as Windows-1252 then saved as UTF-8.
# Fix: decode each file as latin-1 (to get original bytes back), then re-interpret as UTF-8.

files = [f for f in os.listdir(templates_path) if f.endswith('.html')]
for fname in files:
    fpath = os.path.join(templates_path, fname)
    with open(fpath, 'r', encoding='utf-8') as f:
        content = f.read()
    # Encode to latin-1 to get original byte representation, then decode as UTF-8
    try:
        fixed = content.encode('latin-1').decode('utf-8')
        if fixed != content:
            with open(fpath, 'w', encoding='utf-8') as f:
                f.write(fixed)
            print(f'Fixed: {fname}')
        else:
            print(f'No changes: {fname}')
    except (UnicodeDecodeError, UnicodeEncodeError) as e:
        print(f'Error in {fname}: {e}')

