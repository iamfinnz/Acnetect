from flask import Flask

app = Flask(__name__)

@app.route('/', methods=['GET'])
def hello_world():
    return "Hello Ilham"

if __name__ == '__main__':
    app.run(port=3000, debug=True)