
const API_URL = "/api/players";

const playerForm = document.getElementById("playerForm");
const playerTable = document.getElementById("playerTable");
const playerCount = document.getElementById("playerCount");

async function loadPlayers() {
    try {
        const response = await fetch(API_URL);

        if (!response.ok) {
            throw new Error("Failed to load players");
        }

        const players = await response.json();

        playerCount.textContent = players.length;
        playerTable.innerHTML = "";

        players.forEach(player => {
            const row = document.createElement("tr");

            const values = [
                player.id,
                player.name,
                player.position,
                player.team,
                player.jerseyNumber
            ];

            values.forEach(value => {
                const cell = document.createElement("td");
                cell.textContent = value ?? "";
                row.appendChild(cell);
            });

            const actions = document.createElement("td");

            const editButton = document.createElement("button");
            editButton.textContent = "Edit";
            editButton.className = "action-button";
            editButton.onclick = () => editPlayer(player);

            const deleteButton = document.createElement("button");
            deleteButton.textContent = "Delete";
            deleteButton.className = "action-button delete-button";
            deleteButton.onclick = () => deletePlayer(player.id);

            actions.append(editButton, deleteButton);
            row.appendChild(actions);

            playerTable.appendChild(row);
        });

    } catch (error) {
        alert("Unable to load players. Check whether the backend is running.");
        console.error(error);
    }
}

playerForm.addEventListener("submit", async function(event) {
    event.preventDefault();

    const id = document.getElementById("playerId").value;

    const player = {
        name: document.getElementById("name").value,
        position: document.getElementById("position").value,
        team: document.getElementById("team").value,
        jerseyNumber: Number(document.getElementById("jerseyNumber").value)
    };

    try {
        const response = await fetch(
            id ? `${API_URL}/${id}` : API_URL,
            {
                method: id ? "PUT" : "POST",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify(player)
            }
        );

        if (!response.ok) {
            throw new Error("Unable to save player");
        }

        playerForm.reset();
        document.getElementById("playerId").value = "";
        document.getElementById("formTitle").textContent = "Add New Player";
        document.getElementById("submitButton").textContent = "Add Player";

        await loadPlayers();
        alert("Player saved successfully!");

    } catch (error) {
        alert("Error saving player. Check the API.");
        console.error(error);
    }
});

function editPlayer(player) {
    document.getElementById("playerId").value = player.id;
    document.getElementById("name").value = player.name;
    document.getElementById("position").value = player.position;
    document.getElementById("team").value = player.team;
    document.getElementById("jerseyNumber").value = player.jerseyNumber;

    document.getElementById("formTitle").textContent = "Edit Player";
    document.getElementById("submitButton").textContent = "Update Player";

    window.scrollTo({ top: 0, behavior: "smooth" });
}

async function deletePlayer(id) {
    if (!confirm("Are you sure you want to delete this player?")) {
        return;
    }

    try {
        const response = await fetch(`${API_URL}/${id}`, {
            method: "DELETE"
        });

        if (!response.ok) {
            throw new Error("Unable to delete player");
        }

        await loadPlayers();
        alert("Player deleted successfully!");

    } catch (error) {
        alert("Error deleting player.");
        console.error(error);
    }
}

loadPlayers();

