//-------------- WebSocket --------------

var stompClient = null;

function connectToWebSocket() {
    var socket = new SockJS('/ws');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        console.log('Connected: ' + frame);
        stompClient.subscribe('/user/topic/game/update', function (message) {
            console.log('Received message: ' + message.body);
            updateBoard();
        });
        stompClient.subscribe('/user/topic/game/new', function (message) {
            console.log('Received message: ' + message.body);
            fetch('/tic-tac-toe/game/new-game');
        });
        stompClient.subscribe('/user/topic/game/return-to-lobby', function (message) {
            console.log('Received message: ' + message.body);
            updateBoard();
        });
    });
}
connectToWebSocket();



var playersTurn = 'X';
const boardContainer = document.getElementById('board-container');
const playersTurnText = document.getElementById('playersTurnText');
const board = document.getElementById('gameboard');
const fields = document.querySelectorAll('.field');
const gameOverScreen = document.getElementById('game-over-screen');
const whoWonText = document.getElementById('whoWonText');

playersTurnText.textContent = playersTurn + " 's turn";

fields.forEach(field => {
    field.addEventListener("click", () => click(field));
})

function click(field) {
    if (!field.classList.contains('X')
        && !field.classList.contains('O')) {
            const fieldID = field.id;
            const[idRow, idCol] = fieldID.split('-');
            const row = parseInt(idRow);
            const col = parseInt(idCol);

            try {
                move(row, col);
            } catch (error) {
                throw new Error;
            }
    }
}

async function updateBoard() {
    await fetch("/tic-tac-toe/api/game/get-positions-pvp")
    .then(response => response.json())
          .then(data => {
              placePieces(data);
           });

    await fetch('/tic-tac-toe/api/game/which-won-pvp')
        .then(response => response.json())
            .then(data => {
                if (data == 1 || data == 2 || data == 3) {
                    gameWon();
                }
    });
}

async function move(row, col) {
    const moveParams = new URLSearchParams();
        moveParams.append('row', row);
        moveParams.append('col', col);

        await fetch("/tic-tac-toe/api/game/move", {
       method: "POST",
          headers: {
            "Content-Type": "application/x-www-form-urlencoded"
           },
          body: moveParams.toString()
     });
}

function placePieces(locations) {
    for (var i = 0; i < 3; i++) {
        for (var j = 0; j < 3; j++) {
            const fieldID = document.getElementById(i + '-' + j);
            if (fieldID.classList.contains('X')) {
                fieldID.classList.remove('X');
            }
            if (fieldID.classList.contains('O')) {
                fieldID.classList.remove('O');
            }
            if (locations[i][j] == 1) fieldID.classList.add('X');
            if (locations[i][j] == 2) fieldID.classList.add('O');
        }
    }
}

async function gameWon() {
    let users = ["Player", "Computer"];

    await fetch('/tic-tac-toe/api/game/get-lobby-users-pvp')
    .then(response => response.json())
    .then(data => {
        users[0] = data[0];
        users[1] = data[1];
    });

    await fetch('/tic-tac-toe/api/game/which-won-pvp')
    .then(response => response.json())
    .then(data => {
        if (data == 1) {
            gameOverScreen.classList.remove('hidden');
            whoWonText.textContent = users[0] + " Won!"
            boardContainer.classList.add('hidden');
        } else if (data == 2) {
            gameOverScreen.classList.remove('hidden');
            whoWonText.textContent = users[1] + " Won!"
            boardContainer.classList.add('hidden');
        } else if(data == 3) {
            gameOverScreen.classList.remove('hidden');
            whoWonText.textContent = "Draw"
            boardContainer.classList.add('hidden');
        }
    });
}

async function newGame() {
    fetch('/tic-tac-toe/api/game/new-game-pvp');
    await updateBoard();
    gameOverScreen.classList.add('hidden');
    boardContainer.classList.remove('hidden');
    await updateBoard();
}

updateBoard();