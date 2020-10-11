const SocketServer = require('websocket').server
const http = require('http')

const server = http.createServer((req, res) => {})

server.listen(3001, ()=>{
    console.log("Listening on port 3001...")
})

wsServer = new SocketServer({httpServer:server})

var board = "";
const connections = []

wsServer.on('request', (req) => {
    const connection = req.accept()
    console.log('new connection')
    connections.push(connection)

    connection.on('message', (b) => {
		board = b;
        connections.forEach(element => {
            if (element != connection)
                element.sendUTF(board)
        })
    })

    connection.on('close', (resCode, des) => {
        console.log('connection closed')
        connections.splice(connections.indexOf(connection), 1)
    })

})