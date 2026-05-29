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

// Tabs & app elements
const catalogTab = document.getElementById('catalogTab');
const appTab = document.getElementById('appTab');
const catalogPanel = document.getElementById('catalogPanel');
const appPanel = document.getElementById('appPanel');
const appPatternBadge = document.getElementById('appPatternBadge');
const appSearchButton = document.getElementById('appSearchButton');
const appResetButton = document.getElementById('appResetButton');
const appCity = document.getElementById('appCity');
const appNights = document.getElementById('appNights');
const appGuests = document.getElementById('appGuests');
const appListings = document.getElementById('appListings');

let patterns = [];
let patternByCode = new Map();
let currentPattern = null;

const getPatternFromUrl = () => new URLSearchParams(window.location.search).get('pattern');

const showLanding = () => {
    landingView.hidden = false;
    detailView.hidden = true;
    selectedPatternName.textContent = 'Patrón';
    selectedPatternDescription.textContent = '';
};

const showDetail = (pattern) => {
    landingView.hidden = true;
    detailView.hidden = false;
    selectedPatternName.textContent = `${pattern.code} - ${pattern.name}`;
    selectedPatternDescription.textContent = pattern.description;
    patternSelect.value = pattern.code;
};

const navigateToPattern = async (code, replace = false) => {
    const pattern = patternByCode.get(code);
    if (!pattern) {
        return;
    }

    const url = new URL(window.location.href);
    url.searchParams.set('pattern', code);
    if (replace) {
        window.history.replaceState({}, '', url);
    } else {
        window.history.pushState({}, '', url);
    }

    showDetail(pattern);
    await loadDiagram(code);
};

const loadDiagram = async (code) => {
    diagramBox.textContent = 'Cargando diagrama...';
    diagramImage.hidden = true;
    diagramImage.removeAttribute('src');
    try {
        diagramImage.src = `/diagrams/rendered/${code}.png`;
        diagramImage.alt = `Diagrama renderizado del patrón ${code}`;
        diagramImage.hidden = false;

        const response = await fetch(`/diagrams/${code}.puml`);
        if (!response.ok) {
            throw new Error(`HTTP ${response.status}`);
        }
        diagramBox.textContent = await response.text();
    } catch (error) {
        diagramImage.hidden = true;
        diagramBox.textContent = `No se pudo cargar el diagrama: ${error.message}`;
    }
};

const renderCatalog = (patterns) => {
    patternSelect.innerHTML = '';
    catalogGrid.innerHTML = '';

    patterns.forEach((pattern) => {
        const option = document.createElement('option');
        option.value = pattern.code;
        option.textContent = `${pattern.code} - ${pattern.name}`;
        patternSelect.appendChild(option);

        const card = document.createElement('article');
        card.className = 'pattern-card';
        card.tabIndex = 0;
        card.role = 'button';
        card.addEventListener('click', () => navigateToPattern(pattern.code));
        card.addEventListener('keydown', (event) => {
            if (event.key === 'Enter' || event.key === ' ') {
                event.preventDefault();
                navigateToPattern(pattern.code);
            }
        });
        card.innerHTML = `
            <div class="pattern-code">${pattern.code}</div>
            <h3>${pattern.name}</h3>
            <p>${pattern.description}</p>
        `;
        catalogGrid.appendChild(card);
    });
};

const switchTab = (to) => {
    if (to === 'app') {
        catalogTab.setAttribute('aria-selected', 'false');
        appTab.setAttribute('aria-selected', 'true');
        catalogPanel.hidden = true;
        appPanel.hidden = false;
    } else {
        catalogTab.setAttribute('aria-selected', 'true');
        appTab.setAttribute('aria-selected', 'false');
        catalogPanel.hidden = false;
        appPanel.hidden = true;
    }
};

const mockListings = [
    { id: 1, title: 'Apartamento Centro', city: 'Barcelona', price: 90, guests: 2 },
    { id: 2, title: 'Loft Moderno', city: 'Barcelona', price: 120, guests: 4 },
    { id: 3, title: 'Estudio Económico', city: 'Madrid', price: 60, guests: 2 },
];

const patternAppConfigs = new Map();
// Minimal per-pattern scenarios — used to adapt the embedded app UI
patterns.forEach && patterns.clear; // noop to satisfy linters when patterns empty
['01','02','03','04','05','06','07','08','09','10','11','12','13','14','15','16','17','18','19','20','21','22','23'].forEach(code => {
    patternAppConfigs.set(code, {
        scenario: `Escenario hipotético para patrón ${code}`,
        focus: 'default'
    });
});

const applyPatternToApp = (code) => {
    currentPattern = patternByCode.get(code) || null;
    appPatternBadge.textContent = currentPattern ? `${currentPattern.code} - ${currentPattern.name}` : 'Ningún patrón aplicado';
    // adjust UI hints based on a few known patterns
    const cfg = patternAppConfigs.get(code) || { scenario: 'Escenario genérico', focus: 'default' };
    // show a small message in the listings area
    renderAppListings();
    const note = document.createElement('div');
    note.style.color = 'var(--muted)';
    note.style.marginTop = '8px';
    note.textContent = cfg.scenario + ' — enfoque: ' + cfg.focus;
    appPanel.querySelector('.app-form').after(note);
};

const renderAppListings = (filters = {}) => {
    appListings.innerHTML = '';
    const city = (filters.city || appCity.value || '').toLowerCase();
    const nights = parseInt(filters.nights || appNights.value || 1, 10);
    const guests = parseInt(filters.guests || appGuests.value || 1, 10);

    const results = mockListings.filter(l => {
        if (city && !l.city.toLowerCase().includes(city)) return false;
        if (guests && l.guests < guests) return false;
        return true;
    });

    if (results.length === 0) {
        appListings.innerHTML = '<div class="pattern-card">No hay resultados</div>';
        return;
    }

    results.forEach(l => {
        const card = document.createElement('article');
        card.className = 'pattern-card';
        card.innerHTML = `
            <div class="pattern-code">L${l.id}</div>
            <h3>${l.title}</h3>
            <p>${l.city} · $${l.price} por noche</p>
            <div style="display:flex;gap:8px;margin-top:8px">
                <button data-id="${l.id}" class="secondary-button appView">Ver</button>
                <button data-id="${l.id}" class="appUse">Usar</button>
            </div>
        `;
        appListings.appendChild(card);
    });
    // wire actions
    appListings.querySelectorAll('.appView').forEach(btn => btn.addEventListener('click', (e) => {
        const id = e.target.getAttribute('data-id');
        const l = mockListings.find(x => String(x.id) === id);
        alert(`Ver ${l.title} — (demo)`);
    }));
    appListings.querySelectorAll('.appUse').forEach(btn => btn.addEventListener('click', (e) => {
        const id = e.target.getAttribute('data-id');
        // when a listing is used, if a pattern is applied navigate to its detail
        if (currentPattern) {
            navigateToPattern(currentPattern.code);
            switchTab('catalog');
            showDetail(currentPattern);
        } else {
            alert('Aplica primero un patrón desde el catálogo.');
        }
    }));
};

const loadCatalog = async () => {
    try {
        const response = await fetch('/data/patterns.json');
        const payload = await response.json();
        patterns = payload.patterns || [];
        patternByCode = new Map(patterns.map((pattern) => [pattern.code, pattern]));
        renderCatalog(patterns);
        backendStatus.textContent = 'Listo';
    } catch (error) {
        backendStatus.textContent = 'Sin datos';
        outputBox.textContent = `No se pudo cargar el catálogo: ${error.message}`;
    }
};

const checkBackend = async () => {
    try {
        const response = await fetch('/api/patterns');
        const payload = await response.json();
        backendStatus.textContent = `Activo (${payload.patterns.length} patrones)`;
    } catch (error) {
        backendStatus.textContent = 'Desconectado';
    }
};

patternForm.addEventListener('submit', async (event) => {
    event.preventDefault();
    const requestBody = {
        pattern: patternSelect.value,
        input: patternInput.value
    };

    outputBox.textContent = 'Ejecutando patrón...';
    outputMeta.textContent = '';

    try {
        const response = await fetch('/api/patterns/run', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(requestBody)
        });
        const payload = await response.json();
        outputBox.textContent = JSON.stringify(payload, null, 2);
        outputMeta.textContent = payload.success ? 'Ejecución correcta' : 'Ejecución con error';
        await checkBackend();
    } catch (error) {
        outputBox.textContent = `Error ejecutando el backend: ${error.message}`;
        outputMeta.textContent = 'Falló la llamada al servidor';
    }
});

patternSelect.addEventListener('change', () => {
    if (detailView.hidden) {
        return;
    }
    navigateToPattern(patternSelect.value, true);
});

backToCatalog.addEventListener('click', () => {
    const url = new URL(window.location.href);
    url.searchParams.delete('pattern');
    window.history.pushState({}, '', url);
    showLanding();
});

openInApp.addEventListener('click', () => {
    // switch to the embedded app and apply the currently selected pattern
    const code = patternSelect.value || getPatternFromUrl();
    if (!code) {
        alert('Selecciona primero un patrón.');
        return;
    }
    applyPatternToApp(code);
    switchTab('app');
});

catalogTab.addEventListener('click', () => switchTab('catalog'));
appTab.addEventListener('click', () => switchTab('app'));

appSearchButton.addEventListener('click', () => renderAppListings({ city: appCity.value, nights: appNights.value, guests: appGuests.value }));
appResetButton.addEventListener('click', () => { appCity.value=''; appNights.value=2; appGuests.value=2; renderAppListings(); });

window.addEventListener('popstate', () => {
    const code = getPatternFromUrl();
    if (code && patternByCode.has(code)) {
        showDetail(patternByCode.get(code));
        loadDiagram(code);
    } else {
        showLanding();
    }
});

loadCatalog().then(() => {
    const code = getPatternFromUrl();
    if (code && patternByCode.has(code)) {
        showDetail(patternByCode.get(code));
        loadDiagram(code);
    } else {
        showLanding();
    }
    checkBackend();
});