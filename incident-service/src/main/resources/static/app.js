const ASSET_API = "http://localhost:8081/api/assets";
const INCIDENT_API = "/api/incidents";

let assets = [];
let incidents = [];


async function loadData() {

    try {

        const assetResponse = await fetch(ASSET_API);
        assets = await assetResponse.json();

        const incidentResponse = await fetch(INCIDENT_API);
        incidents = await incidentResponse.json();

        renderAssets();
        renderIncidents();
        updateStats();
        updateAssetSelect();

    } catch (error) {

        console.error("Error cargando datos:", error);
    }
}


/* -------------------------
   ACTIVOS
------------------------- */

function renderAssets() {

    const container = document.getElementById("assets");

    container.innerHTML = "";

    assets.forEach(asset => {

        const div = document.createElement("div");

        div.className = "card";

        div.innerHTML = `
            <h3>${asset.name}</h3>

            <p>
                <strong>Tipo:</strong>
                ${asset.type}
            </p>

            <p>
                <strong>Ubicación:</strong>
                ${asset.location}
            </p>

            <p>
                <strong>Criticidad:</strong>
                <span class="badge ${asset.criticality}">
                    ${asset.criticality}
                </span>
            </p>

            <p>
                <strong>Estado:</strong>
                ${asset.status}
            </p>
        `;

        container.appendChild(div);
    });
}


document
    .getElementById("assetForm")
    .addEventListener("submit", async event => {

        event.preventDefault();

        const asset = {

            name:
                document.getElementById("assetName").value,

            type:
                document.getElementById("assetType").value,

            location:
                document.getElementById("assetLocation").value,

            criticality:
                document.getElementById("assetCriticality").value
        };

        const response = await fetch(ASSET_API, {

            method: "POST",

            headers: {
                "Content-Type": "application/json"
            },

            body: JSON.stringify(asset)
        });

        if (response.ok) {

            document
                .getElementById("assetForm")
                .reset();

            await loadData();
        }
    });


/* -------------------------
   INCIDENCIAS
------------------------- */

function renderIncidents() {

    const container =
        document.getElementById("incidents");

    container.innerHTML = "";

    incidents.forEach(incident => {

        const asset =
            assets.find(
                a => a.id === incident.assetId
            );

        const assetName =
            asset ? asset.name : `Activo ${incident.assetId}`;

        const sla =
            incident.slaStatus
                ? `<span class="badge ${incident.slaStatus}">
                       ${incident.slaStatus}
                   </span>`
                : "Pendiente";

        const div =
            document.createElement("div");

        div.className = "card";

        div.innerHTML = `

            <h3>
                #${incident.id} · ${assetName}
            </h3>

            <p>
                ${incident.description}
            </p>

            <p>
                <strong>Severidad:</strong>

                <span class="badge ${incident.severity}">
                    ${incident.severity}
                </span>
            </p>

            <p>
                <strong>Prioridad:</strong>

                <span class="badge ${incident.priority}">
                    ${incident.priority}
                </span>
            </p>

            <p>
                <strong>Estado:</strong>

                <span class="badge ${incident.status}">
                    ${incident.status}
                </span>
            </p>

            <p>
                <strong>SLA:</strong>
                ${incident.slaHours} horas
            </p>

            <p>
                <strong>Deadline:</strong>
                ${formatDate(incident.deadline)}
            </p>

            <p>
                <strong>Cumplimiento SLA:</strong>
                ${sla}
            </p>

            <div class="actions">

                ${
                    incident.status === "OPEN"
                    ?
                    `<button onclick="updateStatus(${incident.id}, 'IN_PROGRESS')">
                        Iniciar
                    </button>`
                    :
                    ""
                }

                ${
                    incident.status === "IN_PROGRESS"
                    ?
                    `<button onclick="updateStatus(${incident.id}, 'RESOLVED')">
                        Resolver
                    </button>`
                    :
                    ""
                }

                ${
                    incident.status === "RESOLVED"
                    ?
                    `<button onclick="updateStatus(${incident.id}, 'CLOSED')">
                        Cerrar
                    </button>`
                    :
                    ""
                }

            </div>
        `;

        container.appendChild(div);
    });
}


document
    .getElementById("incidentForm")
    .addEventListener("submit", async event => {

        event.preventDefault();

        const incident = {

            assetId:
                Number(
                    document.getElementById("incidentAsset").value
                ),

            description:
                document.getElementById("incidentDescription").value,

            serviceImpact:
                document.getElementById("serviceImpact").value,

            safetyImpact:
                document.getElementById("safetyImpact").value,

            workaround:
                document.getElementById("workaround").value
        };

        const response =
            await fetch(INCIDENT_API, {

                method: "POST",

                headers: {
                    "Content-Type": "application/json"
                },

                body:
                    JSON.stringify(incident)
            });

        if (response.ok) {

            document
                .getElementById("incidentForm")
                .reset();

            await loadData();
        }
    });


async function updateStatus(id, status) {

    const response =
        await fetch(
            `${INCIDENT_API}/${id}/status`,
            {

                method: "PATCH",

                headers: {
                    "Content-Type": "application/json"
                },

                body:
                    JSON.stringify({ status })
            }
        );

    if (response.ok) {

        await loadData();
    }
}


/* -------------------------
   ESTADÍSTICAS
------------------------- */

function updateStats() {

    document.getElementById("assetCount")
        .textContent = assets.length;

    document.getElementById("incidentCount")
        .textContent = incidents.length;

    document.getElementById("openCount")
        .textContent =
        incidents.filter(
            i =>
                i.status === "OPEN" ||
                i.status === "IN_PROGRESS"
        ).length;

    document.getElementById("breachedCount")
        .textContent =
        incidents.filter(
            i => i.slaStatus === "BREACHED"
        ).length;
}


/* -------------------------
   UTILIDADES
------------------------- */

function updateAssetSelect() {

    const select =
        document.getElementById("incidentAsset");

    select.innerHTML =
        '<option value="">Seleccionar activo</option>';

    assets.forEach(asset => {

        const option =
            document.createElement("option");

        option.value = asset.id;

        option.textContent =
            `${asset.name} (${asset.criticality})`;

        select.appendChild(option);
    });
}


function formatDate(date) {

    if (!date) {
        return "-";
    }

    return new Date(date)
        .toLocaleString("es-ES");
}


function toggleAssetForm() {

    document
        .getElementById("assetForm")
        .classList.toggle("hidden");
}


function toggleIncidentForm() {

    document
        .getElementById("incidentForm")
        .classList.toggle("hidden");
}


/* Carga inicial */

loadData();