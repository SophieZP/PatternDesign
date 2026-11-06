// =========================
// DOM
// =========================
const backendStatus = document.getElementById('backendStatus');

const patternSelect = document.getElementById('patternSelect');
const patternInput = document.getElementById('patternInput');
const patternForm = document.getElementById('patternForm');

const outputBox = document.getElementById('outputBox');
const outputMeta = document.getElementById('outputMeta');

const catalogGrid = document.getElementById('catalogGrid');

const landingView = document.getElementById('landingView');
const detailView = document.getElementById('detailView');

const selectedPatternName = document.getElementById('selectedPatternName');
const selectedPatternDescription = document.getElementById('selectedPatternDescription');

const diagramImage = document.getElementById('diagramImage');
const diagramBox = document.getElementById('diagramBox');

const backToCatalog = document.getElementById('backToCatalog');
const openInApp = document.getElementById('openInApp');

const catalogTab = document.getElementById('catalogTab');
const appTab = document.getElementById('appTab');

const catalogPanel = document.getElementById('catalogPanel');
const appPanel = document.getElementById('appPanel');

const appPatternBadge = document.getElementById('appPatternBadge');

const appCity = document.getElementById('appCity');
const appNights = document.getElementById('appNights');
const appGuests = document.getElementById('appGuests');
const appListings = document.getElementById('appListings');
const reservationPanel =document.getElementById('reservationPanel');
const toastContainer =document.getElementById('toastContainer');
const appPatternDescription = document.getElementById('appPatternDescription');
const executedPattern = document.getElementById('executedPattern');

// =========================
// STATE (CLAVE: separado)
// =========================
let patterns = [];
let patternByCode = new Map();
let reservations = [];
let reservationHistory = [];

let selectedPattern = null; // catálogo + backend + UML
let appPattern = null;      // mini app (Strategy)

// =========================
// MOCK DATA
// =========================
const mockListings = [
    { id: 1, title: 'Apartamento Centro', city: 'Barcelona', price: 90, guests: 2 },
    { id: 2, title: 'Loft Moderno', city: 'Barcelona', price: 120, guests: 4 },
    { id: 3, title: 'Estudio Económico', city: 'Madrid', price: 60, guests: 2 },
];

// =========================
// STRATEGY (Mini App)
// =========================
const patternBehaviors = {

    // 01 - Singleton
    "01": (l) =>
        l.map(x => ({
            ...x,
            instanceId: "SINGLETON-001"
        })),

    // 02 - Factory Method
    "02": (l) =>
        l.map(x => ({
            ...x,
            createdBy: "PropertyFactory"
        })),

    // 03 - Abstract Factory
    "03": (l) =>
        l.map(x => ({
            ...x,
            family: "Premium"
        })),

    // 04 - Builder
    "04": (l) =>
        l.map(x => ({
            ...x,
            built: true
        })),

    // 05 - Prototype
    "05": (l) => [
        ...l,
        {
            ...l[0],
            id: 999,
            title: l[0].title + " (Clon)",
            cloned: true
        }
    ],

    // 06 - Adapter
    "06": (l) =>
        l.map(x => ({
            ...x,
            adapted: true
        })),

    // 07 - Bridge
    "07": (l) =>
        l.map(x => ({
            ...x,
            bridgeMode: true
        })),

    // 08 - Composite
    "08": (l) =>
        l.map(x => ({
            ...x,
            group: "Luxury Collection"
        })),

    // 09 - Decorator
    "09": (l) =>
        l.map(x => ({
            ...x,
            wifi: true
        })),

    // 10 - Facade
    "10": (l) =>
        l.map(x => ({
            ...x,
            bookingSystem: "Facade API"
        })),

    // 11 - Flyweight
    "11": (l) =>
        l.map(x => ({
            ...x,
            icon: "🏠"
        })),

    // 12 - Proxy
    "12": (l) =>
        l.map(x => ({
            ...x,
            verified: true
        })),

    // 13 - Chain of Responsibility
    "13": (l) =>
        l.map(x => ({
            ...x,
            validationPassed: true
        })),

    // 14 - Command
    "14": (l) =>
        l.map(x => ({
            ...x,
            commandEnabled: true
        })),

    // 15 - Interpreter
    "15": (l, f) =>
        l.filter(x =>
            !f.city ||
            x.city.toLowerCase().includes(f.city)
        ).map(x => ({
            ...x,
            queryParsed: true
        })),

    // 16 - Iterator
    "16": (l) =>
        l.map((x, index) => ({
            ...x,
            iteratorIndex: index + 1
        })),

    // 17 - Mediator
    "17": (l) =>
        l.map(x => ({
            ...x,
            mediatorConnected: true
        })),

    // 18 - Memento
    "18": (l) =>
        l.map(x => ({
            ...x,
            hasSnapshot: true
        })),

    // 19 - Observer
    "19": (l) =>
        l.map(x => ({
            ...x,
            subscribers: 3
        })),

    // 20 - State
    "20": (l) =>
        l.map(x => ({
            ...x,
            state: "Disponible"
        })),

    // 21 - Strategy
    "21": (l, f) =>
        l.filter(x =>
            (!f.city ||
                x.city.toLowerCase().includes(f.city)) &&
            (!f.guests ||
                x.guests >= f.guests)
        ).map(x => ({
            ...x,
            strategy: "GuestFilter"
        })),

    // 22 - Template Method
    "22": (l) =>
        l.map(x => ({
            ...x,
            templateApplied: true
        })),

    // 23 - Visitor
    "23": (l) =>
        l.map(x => ({
            ...x,
            visitorApplied: true
        }))
};

// =========================
// OBSERVER
// =========================

const observers = [];

const subscribe = (fn) => {
    observers.push(fn);
};

const notify = (data) => {
    observers.forEach(fn => fn(data));
};

// =========================
// TOASTS
// =========================

const showToast = (message) => {

    const toast = document.createElement('div');

    toast.style.background = '#222';
    toast.style.color = '#fff';
    toast.style.padding = '10px';
    toast.style.marginBottom = '8px';
    toast.style.borderRadius = '8px';

    toast.textContent = message;

    toastContainer.appendChild(toast);

    setTimeout(() => {
        toast.remove();
    }, 3000);
};

subscribe((reservation) => {

    if (reservation.status === "cancelled") {

        showToast(
            `Reserva cancelada: ${reservation.listing.title}`
        );

    } else {

        showToast(
            `Reserva creada: ${reservation.listing.title}`
        );

    }
});

// =========================
// COMMAND
// =========================

const reservationCommand = {

    execute(listing, context) {

        const reservation = {
            id: Date.now(),
            listing,
            city: context.city,
            guests: context.guests,
            nights: context.nights,
            status: "confirmed"
        };

        reservations.push(reservation);

        reservationHistory.push(
            structuredClone(reservation)
        );

        notify(reservation);

        renderReservations();

        return reservation;
    },

    undo(id) {

        const index =
            reservations.findIndex(
                r => String(r.id) === String(id)
            );

        if (index === -1) return;

        const removed =
            reservations.splice(index, 1)[0];

        notify({
            ...removed,
            status: "cancelled"
        });

        renderReservations();
    }
};

// =========================
// RESERVATIONS UI
// =========================

const renderReservations = () => {

    reservationPanel.innerHTML =
        '<h3>Reservas Activas</h3>';

    if (!reservations.length) {

        reservationPanel.innerHTML +=
            '<p>No hay reservas.</p>';

        return;
    }

    reservations.forEach(r => {

        const div =
            document.createElement('div');

        div.className = 'pattern-card';

        div.innerHTML = `
            <strong>${r.listing.title}</strong>

            <p>
                ${r.city}
                · ${r.guests} huéspedes
                · ${r.nights} noches
            </p>

            <small>${r.status}</small>

            <br><br>

            <button data-id="${r.id}">
                Cancelar
            </button>
        `;

        div.querySelector('button')
            .onclick = () => {

                reservationCommand.undo(r.id);

            };

        reservationPanel.appendChild(div);
    });
};

// =========================
// BACKEND STATUS
// =========================
const checkBackend = async () => {
    try {
        const res = await fetch('/api/patterns');
        const data = await res.json();
        backendStatus.textContent = `Activo (${data.patterns.length} patrones)`;
    } catch {
        backendStatus.textContent = 'Desconectado';
    }
};

// =========================
// LOAD CATALOG
// =========================
const loadCatalog = async () => {
    try {
        const res = await fetch('/data/patterns.json');
        const data = await res.json();

        patterns = data.patterns;
        patternByCode = new Map(patterns.map(p => [p.code, p]));

        renderCatalog();
    } catch (err) {
        outputBox.textContent = "Error cargando catálogo";
        console.error(err);
    }
};

// =========================
// CATALOG RENDER
// =========================
const renderCatalog = () => {
    catalogGrid.innerHTML = '';
    patternSelect.innerHTML = '';

    patterns.forEach(p => {
        const opt = document.createElement('option');
        opt.value = p.code;
        opt.textContent = `${p.code} - ${p.name}`;
        patternSelect.appendChild(opt);

        const card = document.createElement('article');
        card.className = 'pattern-card';
        card.innerHTML = `
            <div class="pattern-code">${p.code}</div>
            <h3>${p.name}</h3>
            <p>${p.description}</p>
        `;

        card.onclick = () => navigateToPattern(p.code);
        catalogGrid.appendChild(card);
    });
};

// =========================
// DETAIL VIEW
// =========================
const showDetail = (p) => {
    landingView.hidden = true;
    detailView.hidden = false;

    selectedPatternName.textContent = `${p.code} - ${p.name}`;
    selectedPatternDescription.textContent = p.description;

    patternSelect.value = p.code;
};

const showLanding = () => {
    landingView.hidden = false;
    detailView.hidden = true;
};

// =========================
// NAVIGATION + UML
// =========================
const navigateToPattern = async (code) => {
    const pattern = patternByCode.get(code);
    if (!pattern) return;

    selectedPattern = pattern;

    showDetail(pattern);
    loadDiagram(code);
};

// =========================
// UML LOADER (FIX FINAL)
// =========================
const loadDiagram = async (code) => {

    diagramImage.src =
        `/diagrams/rendered/${code}.png`;

    diagramImage.hidden = false;

    try {

        const res =
            await fetch(`/diagrams/${code}.puml`);

        if (res.ok) {
            diagramBox.textContent =
                await res.text();
        }

    } catch {

        diagramBox.textContent =
            'No se pudo cargar el PUML';

    }
};

// =========================
// BACKEND EXECUTION (ESTABLE)
// =========================
patternForm.addEventListener('submit', async (e) => {
    e.preventDefault();

    if (!selectedPattern) return;

    outputBox.textContent = "Ejecutando...";
    outputMeta.textContent = "";

    try {
        const res = await fetch('/api/patterns/run', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({
                pattern: selectedPattern.code,
                input: patternInput.value
            })
        });

        const data = await res.json();

        outputBox.textContent = JSON.stringify(data, null, 2);
        outputMeta.textContent = data.success ? "OK" : "Error";

        if (executedPattern) {
            executedPattern.textContent = `Patrón ejecutado: ${selectedPattern.name}`;
        }

        loadDiagram(selectedPattern.code);
        checkBackend();

    } catch (err) {
        outputBox.textContent = err.message;
    }
});

// =========================
// MINI APP
// =========================
const renderAppListings = () => {

    appListings.innerHTML = '';

    const city = appCity.value.toLowerCase();
    const guests = parseInt(appGuests.value || 1);

    let data = mockListings;

    const behavior =
        appPattern
            ? patternBehaviors[appPattern.code]
            : null;

    if (behavior) {
        data = behavior(data, { city, guests });
    }

    if (!data.length) {

        appListings.innerHTML =
            `<div class="pattern-card">Sin resultados</div>`;

        return;
    }

    data.forEach(l => {

        const card =
            document.createElement('article');

        card.className = 'pattern-card';

        const title =
            l.title || l.name || "Sin nombre";

        const listingCity =
            l.city || "N/A";

        const price =
            l.price || l.cost || 0;

            let extras = '';

            if (l.instanceId)
                extras += `<p>Singleton: ${l.instanceId}</p>`;

            if (l.createdBy)
                extras += `<p>Factory: ${l.createdBy}</p>`;

            if (l.family)
                extras += `<p>Familia: ${l.family}</p>`;

            if (l.built)
                extras += `<p>🔨 Construido con Builder</p>`;

            if (l.cloned)
                extras += `<p>🧬 Clonado desde Prototype</p>`;

            if (l.adapted)
                extras += `<p>🔌 Adaptado</p>`;

            if (l.bridgeMode)
                extras += `<p>🌉 Bridge activo</p>`;

            if (l.group)
                extras += `<p>📦 Grupo: ${l.group}</p>`;

            if (l.wifi)
                extras += `<p>📶 Wifi incluido</p>`;

            if (l.bookingSystem)
                extras += `<p>⚙️ ${l.bookingSystem}</p>`;

            if (l.icon)
                extras += `<p>${l.icon} Flyweight</p>`;

            if (l.verified)
                extras += `<p>🛡️ Verificado por Proxy</p>`;

            if (l.validationPassed)
                extras += `<p>✅ Validado</p>`;

            if (l.commandEnabled)
                extras += `<p>⌨️ Command habilitado</p>`;

            if (l.queryParsed)
                extras += `<p>📝 Consulta interpretada</p>`;

            if (l.iteratorIndex !== undefined)
                extras += `<p>🔄 Posición ${l.iteratorIndex}</p>`;

            if (l.mediatorConnected)
                extras += `<p>🤝 Mediator conectado</p>`;

            if (l.hasSnapshot)
                extras += `<p>💾 Snapshot disponible</p>`;

            if (l.subscribers)
                extras += `<p>👀 Observadores: ${l.subscribers}</p>`;

            if (l.state)
                extras += `<p>📍 Estado: ${l.state}</p>`;

            if (l.strategy)
                extras += `<p>🎯 Estrategia: ${l.strategy}</p>`;

            if (l.templateApplied)
                extras += `<p>📋 Template Method aplicado</p>`;

            if (l.visitorApplied)
                extras += `<p>🚶 Visitor ejecutado</p>`;

        card.innerHTML = `
            <h3>${title}</h3>
            <p>${listingCity} - $${price}</p>

            ${extras}

            <button class="reserveBtn">
                Reservar
            </button>
        `;

        card.querySelector('.reserveBtn')
            .onclick = () => {

                reservationCommand.execute(
                    l,
                    {
                        city: listingCity,
                        guests: l.guests || guests,
                        nights: Number(appNights.value)
                    }
                );

            };

        appListings.appendChild(card);
    });
};

// =========================
// APPLY PATTERN (MINI APP)
// =========================

const applyPatternToApp = (code) => {

    const pattern = patternByCode.get(code);

    if (!pattern) return;

    appPattern = pattern;

    appPatternBadge.textContent =
        `${pattern.code} - ${pattern.name}`;

    if (appPatternDescription) {
        appPatternDescription.textContent =
            pattern.description;
    }

    const oldInfo =
        document.getElementById('patternInfo');

    if (oldInfo) oldInfo.remove();

    const info = document.createElement('div');

    info.id = 'patternInfo';
    info.className = 'pattern-card';

    info.innerHTML = `
        <h3>${pattern.name}</h3>
        <p>${pattern.description}</p>
    `;

    appListings.before(info);

    renderAppListings();

};

// =========================
// EVENTS
// =========================
patternSelect.onchange = () => {
    navigateToPattern(patternSelect.value);
};

backToCatalog.onclick = () => {

    showLanding();

    selectedPattern = null;

    selectedPatternName.textContent = 'Patrón';
    selectedPatternDescription.textContent = '';

    diagramImage.hidden = true;

    diagramBox.textContent =
        'Selecciona un patrón para ver su diagrama.';
};
openInApp.onclick = () => {
    if (!selectedPattern) return;

    applyPatternToApp(selectedPattern.code);
    switchTab('app');
};

catalogTab.onclick = () => switchTab('catalog');
appTab.onclick = () => {

    switchTab('app');

    if (!appPattern) {

        appPatternBadge.textContent =
            'Selecciona un patrón desde el catálogo';

        renderAppListings();
    }
};

document.getElementById('appSearchButton')
    .onclick = renderAppListings;

document.getElementById('appResetButton')
    .onclick = () => {

        console.log("RESET");

        appCity.value = '';
        appNights.value = 2;
        appGuests.value = 2;

        appPattern = null;

        appPatternBadge.textContent =
            'Ningún patrón aplicado';

        const info =
            document.getElementById('patternInfo');

        if (info) {
            info.remove();
        }

        renderAppListings();

    };

// =========================
// TABS
// =========================
const switchTab = (tab) => {
    catalogPanel.hidden = tab !== 'catalog';
    appPanel.hidden = tab !== 'app';
};

// =========================
// INIT
// =========================
loadCatalog().then(() => {

    checkBackend();

    renderReservations();

});