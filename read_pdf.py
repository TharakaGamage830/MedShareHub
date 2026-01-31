import pdfplumber
import sys

def read_pdf(filepath):
    """Extract text from PDF file"""
    with pdfplumber.open(filepath) as pdf:
        text = ""
        for page in pdf.pages:
            text += page.extract_text() + "\n\n"
    return text

if __name__ == "__main__":
    if len(sys.argv) < 2:
        print("Usage: python read_pdf.py <pdf_file>")
        sys.exit(1)
    
    pdf_file = sys.argv[1]
    content = read_pdf(pdf_file)
    print(content)
