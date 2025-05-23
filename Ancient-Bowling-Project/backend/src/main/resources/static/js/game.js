let gameId = null;
let players = [];
let currentPlayer = null;

function updateGameStatus() {
    const statusDiv = document.getElementById('gameStatus');
    statusDiv.className = 'game-status ' + (gameId ? 'active' : 'inactive');
    document.getElementById('gameId').innerText = gameId || 'None';
}

function updatePlayerList() {
    const playerList = document.getElementById('playerList');
    playerList.innerHTML = '<h4>Players:</h4>' +
        players.map(player => `
                    <div class="player-item">
                        ${player}
                        ${currentPlayer === player ? '<span class="badge bg-primary">Current</span>' : ''}
                    </div>
                `).join('');
}

function renderScoreboard(data) {
    const scoreboard = document.getElementById('scoreboard');
    if (!data || !data.length) {
        scoreboard.innerHTML = '<div class="p-3">No scores available</div>';
        return;
    }

    let html = '<div class="scoreboard-header">Current Standings</div>';
    data.forEach((playerScore, index) => {
        html += `
                    <div class="p-3 ${index % 2 === 0 ? 'bg-light' : ''}">
                        <h5>${playerScore.name}</h5>
                        <div class="frames">
                            ${renderFrames(playerScore.player)}
                        </div>
                        <div class="mt-2">
                            Total Score: <strong>${playerScore.score}</strong>
                        </div>
                    </div>
                `;
    });
    scoreboard.innerHTML = html;
}

function renderFrames(player) {
    return player.frames.map((frame, index) => {
        console.log(`Frame ${index + 1}:`, frame);

        // const remainingPins = 15 - frame.throws.reduce((sum, t) => sum + t, 0);
        const isSpare = frame.throws.length > 1 && frame.throws.reduce((sum, t) => sum + t, 0) === 15;

        return `
                    <div class="frame ${frame.isCurrentFrame ? 'current-frame' : ''}">
                        <div>Frame ${index + 1}</div>
                        <div class="throws">
                            ${frame.throws.map((t, throwIndex) => {
            if (t === 15) return '<span class="strike">X</span>';
            if (isSpare && throwIndex === frame.throws.length - 1) return '<span class="spare">/</span>';
            return t;
        }).join(' ')}
                        </div>
                        <div class="remaining-pins mt-1">
                            Remaining pins: ${frame.remainingPins}
                        </div>
                    </div>
                `;
    }).join('');
}

async function createGame() {
    try {
        const res = await fetch('/api/games', {method: 'POST'});
        gameId = await res.text();
        players = [];
        currentPlayer = null;
        updateGameStatus();
        updatePlayerList();
    } catch (error) {
        alert('Error creating game: ' + error.message);
    }
}

async function addPlayer() {
    const playerName = document.getElementById('playerName').value.trim();
    if (!playerName || !gameId) {
        alert("Please enter a player name and create a game first");
        return;
    }

    try {
        await fetch(`/api/games/${gameId}/players?playerName=${encodeURIComponent(playerName)}`, {
            method: 'POST'
        });
        players.push(playerName);
        document.getElementById('playerName').value = '';
        updatePlayerList();
    } catch (error) {
        alert('Error adding player: ' + error.message);
    }
}

async function startGame() {
    if (!gameId) {
        alert("Please create a game first");
        return;
    }

    try {
        await fetch(`/api/games/${gameId}/start`, {method: 'POST'});
        refreshScoreboard();
    } catch (error) {
        alert('Error starting game: ' + error.message);
    }
}

async function makeThrow() {
    const pins = document.getElementById('throwPins').value;
    if (!gameId || pins === '') {
        alert("Please enter number of pins");
        return;
    }

    try {
        await fetch(`/api/games/${gameId}/throw?pins=${pins}`, {
            method: 'POST'
        });
        document.getElementById('throwPins').value = '';
        refreshScoreboard();
    } catch (error) {
        alert('Error making throw: ' + error.message);
    }
}

async function refreshScoreboard() {
    if (!gameId) return;

    try {
        const [scoreboardRes, gameRes] = await Promise.all([
            fetch(`/api/games/${gameId}/scoreboard`),
            fetch(`/api/games/${gameId}`)
        ]);
        const scoreboard = await scoreboardRes.json();
        const game = await gameRes.json();

        currentPlayer = game.currentPlayer?.name;
        renderScoreboard(scoreboard);
        updatePlayerList();
    } catch (error) {
        console.error('Error refreshing scoreboard:', error);
    }
}

// Initial status update
updateGameStatus();