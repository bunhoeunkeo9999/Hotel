package com.example.gateway.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.net.URI;

@RestController
@RequestMapping("/api")
public class ApiGatewayController {

    private final RestTemplate restTemplate = new RestTemplate();

    private URI serviceUri(String service, String path) {
        return URI.create(String.format("http://%s:8080%s", service, path));
    }

    private ResponseEntity<String> proxy(HttpMethod method, String service, String path, HttpHeaders headers, String body) {
        RequestEntity<String> request = new RequestEntity<>(body, headers, method, serviceUri(service, path));
        return restTemplate.exchange(request, String.class);
    }

    @GetMapping(value = {"/rooms", "/rooms/"}, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> proxyRoomsJson(RequestEntity<String> request) {
        return proxy(request.getMethod(), "room-service", request.getUrl().getPath(), request.getHeaders(), request.getBody());
    }

    @GetMapping(value = {"/rooms", "/rooms/"}, produces = MediaType.TEXT_HTML_VALUE)
    public ResponseEntity<String> roomsUi() {
        String html = """
                <!DOCTYPE html>
                <html lang=\"en\">
                <head>
                    <meta charset=\"UTF-8\">
                    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">
                    <title>Hotel Room Manager</title>
                    <style>
                        :root {
                            color-scheme: dark;
                            --surface: #0f172a;
                            --surface-2: #111f3f;
                            --surface-3: #172d4d;
                            --text: #e6f7ff;
                            --muted: #a5c6e8;
                            --accent: #4ade80;
                            --accent-strong: #38bdf8;
                            --border: rgba(255,255,255,0.08);
                            --danger: #fb7185;
                        }
                        body {
                            font-family: Inter, Segoe UI, system-ui, sans-serif;
                            margin: 0;
                            min-height: 100vh;
                            background: radial-gradient(circle at top, rgba(56, 189, 248, 0.12), transparent 40%),
                                        linear-gradient(180deg, #061525 0%, #081a34 100%);
                            color: var(--text);
                        }
                        .page-container {
                            padding: 32px 24px;
                            max-width: 960px;
                            margin: 0 auto;
                        }
                        main {
                            display: grid;
                            gap: 24px;
                        }
                        h1 {
                            margin-bottom: 0.2em;
                            font-size: clamp(2rem, 2.5vw, 3rem);
                            letter-spacing: -0.03em;
                            text-align: center;
                        }
                        section {
                            margin-top: 32px;
                            max-width: 920px;
                            margin: 32px auto 0 auto;
                        }
                        p.description {
                            color: var(--muted);
                            margin-bottom: 24px;
                            text-align: center;
                        }
                        h2 {
                            text-align: center;
                            color: var(--muted);
                            margin-top: 0;
                        }
                        form {
                            margin: 0 auto 28px auto;
                            display: grid;
                            gap: 16px;
                            max-width: 920px;
                            background: rgba(15, 23, 42, 0.88);
                            border: 1px solid var(--border);
                            border-radius: 24px;
                            padding: 24px;
                            box-shadow: 0 30px 75px rgba(0, 0, 0, 0.18);
                        }
                        label {
                            display: grid;
                            gap: 8px;
                            font-weight: 500;
                            color: var(--text);
                        }
                        input, select {
                            padding: 14px 16px;
                            font-size: 1rem;
                            width: 100%;
                            border-radius: 14px;
                            border: 1px solid rgba(148, 163, 184, 0.24);
                            background: #0b1430;
                            color: var(--text);
                            box-sizing: border-box;
                        }
                        button {
                            padding: 14px 20px;
                            font-size: 1rem;
                            cursor: pointer;
                            border: none;
                            border-radius: 14px;
                            background: linear-gradient(135deg, var(--accent-strong), #22c55e);
                            color: #0f172a;
                            font-weight: 700;
                            transition: transform 0.2s ease, box-shadow 0.2s ease;
                        }
                        button:hover {
                            transform: translateY(-1px);
                            box-shadow: 0 18px 30px rgba(56, 189, 248, 0.18);
                        }
                        table {
                            border-collapse: separate;
                            border-spacing: 0;
                            width: 100%;
                            max-width: 900px;
                            margin: 0 auto;
                            background: rgba(15, 23, 42, 0.9);
                            border: 1px solid var(--border);
                            border-radius: 24px;
                            overflow: hidden;
                            box-shadow: 0 30px 75px rgba(0, 0, 0, 0.16);
                        }
                        th, td {
                            padding: 16px 18px;
                            text-align: left;
                        }
                        th {
                            background: #0d1a37;
                            font-size: 0.95rem;
                            letter-spacing: 0.04em;
                            color: #cfe3ff;
                        }
                        tr:nth-child(even) {
                            background: rgba(148, 163, 184, 0.05);
                        }
                        td {
                            border-top: 1px solid rgba(255,255,255,0.05);
                            color: var(--text);
                        }
                        .status-select, .edit-input {
                            width: 100%;
                            background: #0b1430;
                        }
                        .message {
                            margin-top: 12px;
                            color: var(--accent);
                            min-height: 1.2rem;
                        }
                        .row-actions {
                            display: flex;
                            gap: 8px;
                        }
                    </style>
                </head>
                <body>
                <div class=\"page-container\">
                    <main>
                        <div>
                            <h1>Hotel Room Manager</h1>
                            <p class=\"description\">Add new rooms, update prices, room numbers, and status from a single dashboard.</p>
                        </div>
                        <form id=\"room-form\">
                            <label>Room number<input type=\"text\" id=\"room-number\" required></label>
                            <label>Type<input type=\"text\" id=\"room-type\" required></label>
                            <label>Price<input type=\"number\" step=\"0.01\" id=\"room-price\" required></label>
                            <label>Guest name<input type=\"text\" id=\"room-guest\" placeholder=\"Optional\"></label>
                            <label>Status
                                <select id=\"room-status\">
                                    <option>Available</option>
                                    <option>Occupied</option>
                                    <option>Cleaning</option>
                                    <option>Maintenance</option>
                                </select>
                            </label>
                            <button type=\"submit\">Add Room</button>
                            <div class=\"message\" id=\"form-message\"></div>
                        </form>
                        <section>
                            <h2>Existing Rooms</h2>
                            <table id=\"room-table\">
                                <thead>
                                    <tr><th>ID</th><th>Number</th><th>Type</th><th>Price</th><th>Guest</th><th>Status</th><th>Actions</th></tr>
                                </thead>
                                <tbody></tbody>
                            </table>
                            <div class=\"message\" id=\"table-message\"></div>
                        </section>
                    </main>
                </div>
                <script>
                    const apiBase = '/api/rooms';
                    const form = document.getElementById('room-form');
                    const message = document.getElementById('form-message');
                    const tableBody = document.querySelector('#room-table tbody');
                    const tableMessage = document.getElementById('table-message');

                    async function loadRooms() {
                        try {
                            const response = await fetch(apiBase, {headers: {Accept: 'application/json'}});
                            if (!response.ok) throw new Error('Unable to load rooms');
                            const rooms = await response.json();
                            tableBody.innerHTML = rooms.map(room => `
                                <tr>
                                    <td>${room.id}</td>
                                    <td><input class=\"edit-input\" data-field=\"number\" data-id=\"${room.id}\" value=\"${room.number}\"></td>
                                    <td><input class=\"edit-input\" data-field=\"type\" data-id=\"${room.id}\" value=\"${room.type}\"></td>
                                    <td><input class=\"edit-input\" data-field=\"price\" data-id=\"${room.id}\" type=\"number\" step=\"0.01\" value=\"${room.price}\"></td>
                                    <td><input class=\"edit-input\" data-field=\"guestName\" data-id=\"${room.id}\" value=\"${room.guestName || ''}\"></td>
                                    <td>
                                        <select class=\"status-select\" data-id=\"${room.id}\">
                                            ${['Available','Occupied','Cleaning','Maintenance'].map(status => `<option value=\"${status}\" ${status===room.status?'selected':''}>${status}</option>`).join('')}
                                        </select>
                                    </td>
                                    <td class=\"row-actions\">
                                        <button type=\"button\" data-action=\"save\" data-id=\"${room.id}\">Save</button>
                                    </td>
                                </tr>
                            `).join('');
                            if (!rooms.length) tableMessage.textContent = 'No rooms available yet.';
                            else tableMessage.textContent = '';
                            bindRowEvents();
                        } catch (error) {
                            tableMessage.textContent = error.message;
                        }
                    }

                    function bindRowEvents() {
                        document.querySelectorAll('.status-select').forEach(select => {
                            select.addEventListener('change', event => {
                                const id = event.target.dataset.id;
                                saveRoom(id);
                            });
                        });
                        document.querySelectorAll('input.edit-input').forEach(input => {
                            input.addEventListener('blur', event => {
                                const id = event.target.dataset.id;
                                saveRoom(id);
                            });
                            input.addEventListener('keydown', event => {
                                if (event.key === 'Enter') {
                                    event.preventDefault();
                                    saveRoom(event.target.dataset.id);
                                }
                            });
                        });
                        document.querySelectorAll('button[data-action="save"]').forEach(button => {
                            button.addEventListener('click', () => saveRoom(button.dataset.id));
                        });
                    }

                    async function saveRoom(id) {
                        const number = document.querySelector(`input[data-field=\"number\"][data-id=\"${id}\"]`).value;
                        const type = document.querySelector(`input[data-field=\"type\"][data-id=\"${id}\"]`).value;
                        const price = parseFloat(document.querySelector(`input[data-field=\"price\"][data-id=\"${id}\"]`).value);
                        const guestName = document.querySelector(`input[data-field=\"guestName\"][data-id=\"${id}\"]`).value;
                        const status = document.querySelector(`select[data-id=\"${id}\"]`).value;
                        try {
                            const response = await fetch(`${apiBase}/${id}`, {
                                method: 'PUT',
                                headers: {'Content-Type': 'application/json', 'Accept': 'application/json'},
                                body: JSON.stringify({number, type, price, status, guestName})
                            });
                            if (!response.ok) throw new Error('Unable to save room');
                            const updated = await response.json();
                            message.textContent = `Saved room ${updated.number} (ID ${updated.id}).`;
                            loadRooms();
                        } catch (error) {
                            message.textContent = error.message;
                        }
                    }

                    form.addEventListener('submit', async event => {
                        event.preventDefault();
                        const room = {
                            number: document.getElementById('room-number').value,
                            type: document.getElementById('room-type').value,
                            price: parseFloat(document.getElementById('room-price').value),
                            guestName: document.getElementById('room-guest').value,
                            status: document.getElementById('room-status').value
                        };
                        try {
                            const response = await fetch(apiBase, {
                                method: 'POST',
                                headers: {'Content-Type': 'application/json', 'Accept': 'application/json'},
                                body: JSON.stringify(room)
                            });
                            if (!response.ok) throw new Error('Unable to add room');
                            const created = await response.json();
                            message.textContent = `Created room ${created.number} (ID ${created.id}).`;
                            form.reset();
                            loadRooms();
                        } catch (error) {
                            message.textContent = error.message;
                        }
                    });

                    loadRooms();
                </script>
                </body>
                </html>
                """;
        return ResponseEntity.ok().contentType(MediaType.TEXT_HTML).body(html);
    }

    @GetMapping(value = {"/housekeeping/tasks", "/housekeeping/tasks/"}, produces = MediaType.TEXT_HTML_VALUE)
    public ResponseEntity<String> housekeepingUi() {
        String html = """
                <!DOCTYPE html>
                <html lang=\"en\">
                <head>
                    <meta charset=\"UTF-8\">
                    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">
                    <title>Housekeeping Tasks</title>
                    <style>
                        :root { color-scheme: dark; --surface: #0f172a; --text: #e6f7ff; --muted: #a5c6e8; --border: rgba(255,255,255,0.08); --accent: #4ade80; }
                        body { font-family: Inter, Segoe UI, system-ui, sans-serif; margin: 0; min-height: 100vh; background: linear-gradient(180deg, #061525 0%, #081a34 100%); color: var(--text); }
                        .page-container { padding: 32px 24px; max-width: 960px; margin: 0 auto; }
                        h1 { text-align: center; margin-bottom: 0.2em; font-size: clamp(1.6rem, 2.2vw, 2.4rem); }
                        p.description { text-align: center; color: var(--muted); margin-bottom: 18px; }
                        table { width: 100%; max-width: 920px; margin: 0 auto; border-collapse: collapse; background: rgba(15,23,42,0.9); border-radius: 12px; overflow: hidden; }
                        th, td { padding: 12px 14px; text-align: left; }
                        th { background: #0d1a37; color: #cfe3ff; }
                        tr:nth-child(even) td { background: rgba(148,163,184,0.03); }
                        .assign-input,
                        .assign-notes {
                            padding: 12px 14px;
                            border-radius: 14px;
                            border: 1px solid rgba(148,163,184,0.18);
                            background: #071026;
                            color: var(--text);
                            width: 100%;
                            box-sizing: border-box;
                        }
                        .assign-btn,
                        .finish-btn {
                            padding: 10px 14px;
                            border-radius: 14px;
                            border: none;
                            background: linear-gradient(135deg, #38bdf8, #22c55e);
                            color: #061525;
                            font-weight: 700;
                            cursor: pointer;
                            transition: transform 0.15s ease, box-shadow 0.15s ease;
                        }
                        .assign-btn:hover,
                        .finish-btn:hover {
                            transform: translateY(-1px);
                            box-shadow: 0 14px 24px rgba(56,189,248,0.18);
                        }
                        .assign-row {
                            display: flex;
                            gap: 10px;
                            align-items: center;
                        }
                        .assign-row .assign-input {
                            flex: 1;
                        }
                        .finish-btn {
                            min-width: 110px;
                        }
                        .assigned-section {
                            margin-top: 24px;
                        }
                        .assigned-section h2 {
                            margin-bottom: 14px;
                        }
                        .input-cell {
                            min-width: 180px;
                        }
                        .small { font-size: 0.9rem; color: var(--muted); }
                        .message { text-align: center; margin-top: 12px; color: var(--accent); min-height: 1.2rem; }
                        .no-items { text-align: center; color: var(--muted); }
                    </style>
                </head>
                <body>
                <div class=\"page-container\">
                    <h1>Housekeeping — Maintenance Assignments</h1>
                    <p class=\"description\">Rooms currently in <strong>Maintenance</strong>. Assign staff to start cleaning; assignment will update room status to <em>Cleaning</em>.</p>
                    <section>
                        <table id=\"hk-table\">
                            <thead>
                                <tr><th>#</th><th>Room</th><th>Type</th><th>Price</th><th>Notes</th><th>Assign</th></tr>
                            </thead>
                            <tbody></tbody>
                        </table>
                        <div class=\"message\" id=\"hk-message\"></div>
                    </section>
                    <section class=\"assigned-section\">
                        <h2>Assigned Work</h2>
                        <table id=\"assigned-table\">
                            <thead>
                                <tr><th>#</th><th>Room</th><th>Staff</th><th>Notes</th><th>Status</th><th>Action</th></tr>
                            </thead>
                            <tbody></tbody>
                        </table>
                    </section>
                </div>
                <script>
                    const apiRooms = '/api/rooms';
                    const hkApi = '/api/housekeeping/tasks';
                    const tbody = document.querySelector('#hk-table tbody');
                    const msg = document.getElementById('hk-message');
                    let refreshPaused = false;

                    function isEditing() {
                        const active = document.activeElement;
                        return active && active.matches('.assign-input, .assign-notes');
                    }

                    function setTypingHandlers() {
                        document.querySelectorAll('.assign-input, .assign-notes').forEach(el => {
                            el.addEventListener('focus', () => refreshPaused = true);
                            el.addEventListener('blur', () => refreshPaused = false);
                        });
                    }

                    async function loadMaintenanceRooms() {
                        try {
                            const res = await fetch(apiRooms, {headers: {Accept: 'application/json'}});
                            if (!res.ok) throw new Error('Failed to load rooms');
                            const rooms = await res.json();
                            const maintenance = rooms.filter(r => r.status === 'Maintenance');
                            if (!maintenance.length) {
                                tbody.innerHTML = '<tr><td colspan=6 class="no-items">No rooms in Maintenance</td></tr>';
                                msg.textContent = '';
                                return;
                            }
                            const savedValues = {};
                            document.querySelectorAll('.assign-input, .assign-notes').forEach(el => {
                                const id = el.dataset.id;
                                if (!savedValues[id]) savedValues[id] = {};
                                if (el.classList.contains('assign-input')) savedValues[id].staff = el.value;
                                if (el.classList.contains('assign-notes')) savedValues[id].notes = el.value;
                            });
                            tbody.innerHTML = maintenance.map((r, i) => `
                                <tr>
                                    <td>${i+1}</td>
                                    <td>${r.number}</td>
                                    <td>${r.type || ''}</td>
                                    <td>${r.price ?? ''}</td>
                                    <td class="input-cell">
                                        <input class="assign-notes" data-id="${r.id}" placeholder="Notes" value="${savedValues[r.id]?.notes ?? ''}" />
                                    </td>
                                    <td class="input-cell">
                                        <div class="assign-row">
                                            <input class="assign-input" data-id="${r.id}" placeholder="Staff name" value="${savedValues[r.id]?.staff ?? ''}" />
                                            <button class="assign-btn" data-id="${r.id}">Assign</button>
                                        </div>
                                    </td>
                                </tr>
                            `).join('');
                            bindAssign();
                            setTypingHandlers();
                            msg.textContent = '';
                        } catch (e) {
                            msg.textContent = e.message;
                        }
                    }

                    function bindAssign() {
                        document.querySelectorAll('.assign-btn').forEach(btn => {
                            btn.addEventListener('click', async () => {
                                const id = btn.dataset.id;
                                const input = document.querySelector(`.assign-input[data-id="${id}"]`);
                                const staff = (input.value || '').trim();
                                if (!staff) { msg.textContent = 'Please enter a staff name.'; return; }
                                msg.textContent = 'Assigning...';
                                try {
                                    const rres = await fetch(`${apiRooms}/${id}`, {headers: {Accept: 'application/json'}});
                                    if (!rres.ok) throw new Error('Failed to fetch room');
                                    const room = await rres.json();
                                    const notesInput = document.querySelector(`.assign-notes[data-id="${id}"]`);
                                    const notes = (notesInput && notesInput.value) ? notesInput.value.trim() : '';
                                    const task = { roomNumber: room.number, status: 'Cleaning', assignedStaff: staff, notes: notes };
                                    const tRes = await fetch(hkApi, { method: 'POST', headers: {'Content-Type':'application/json', Accept: 'application/json'}, body: JSON.stringify(task) });
                                    if (!tRes.ok) throw new Error('Failed to create housekeeping task');
                                    const updatedRoom = { number: room.number, type: room.type, price: room.price, status: 'Cleaning' };
                                    const uRes = await fetch(`${apiRooms}/${id}`, { method: 'PUT', headers: {'Content-Type':'application/json', Accept: 'application/json'}, body: JSON.stringify(updatedRoom) });
                                    if (!uRes.ok) throw new Error('Failed to update room status');
                                    msg.textContent = `Assigned ${staff} to room ${room.number}`;
                                    loadMaintenanceRooms();
                                    loadAssignedTasks();
                                } catch (err) {
                                    msg.textContent = err.message;
                                }
                            });
                        });

                    }

                    async function loadAssignedTasks() {
                        try {
                            const r = await fetch(hkApi, {headers: {Accept: 'application/json'}});
                            if (!r.ok) throw new Error('Failed to load housekeeping tasks');
                            const tasks = await r.json();
                            const assigned = tasks.filter(t => t.assignedStaff && t.assignedStaff.trim() !== '' && t.status === 'Cleaning');
                            const atBody = document.querySelector('#assigned-table tbody');
                            if (!assigned.length) {
                                atBody.innerHTML = '<tr><td colspan=6 class="no-items">No assigned work</td></tr>';
                                return;
                            }
                            atBody.innerHTML = assigned.map((t, i) => `
                                <tr>
                                    <td>${i+1}</td>
                                    <td>${t.roomNumber}</td>
                                    <td>${t.assignedStaff}</td>
                                    <td>${t.notes || ''}</td>
                                    <td>${t.status}</td>
                                    <td><button class="finish-btn" data-taskid="${t.id}" data-roomnumber="${t.roomNumber}">Finished</button></td>
                                </tr>
                            `).join('');
                            bindFinish();
                        } catch (err) {
                            console.error(err);
                        }
                    }

                    function bindFinish() {
                        document.querySelectorAll('.finish-btn').forEach(btn => {
                            btn.addEventListener('click', async () => {
                                const taskId = btn.dataset.taskid;
                                const roomNumber = btn.dataset.roomnumber;
                                msg.textContent = 'Finishing work...';
                                try {
                                    // Update housekeeping task: unassign staff and mark Clean
                                    const tRes = await fetch(`${hkApi}/${taskId}`, {
                                        method: 'PUT',
                                        headers: {'Content-Type':'application/json', Accept: 'application/json'},
                                        body: JSON.stringify({ roomNumber: roomNumber, status: 'Clean', assignedStaff: '', notes: '' })
                                    });
                                    if (!tRes.ok) throw new Error('Failed to update task');
                                    // Find corresponding room id by number and set it Available
                                    const roomsRes = await fetch(apiRooms, {headers: {Accept: 'application/json'}});
                                    if (!roomsRes.ok) throw new Error('Failed to load rooms');
                                    const rooms = await roomsRes.json();
                                    const room = rooms.find(r => String(r.number) === String(roomNumber));
                                    if (room) {
                                        const updatedRoom = { number: room.number, type: room.type, price: room.price, guestName: room.guestName || '', status: (room.guestName && room.guestName.trim() !== '') ? 'Occupied' : 'Available' };
                                        const uRes = await fetch(`${apiRooms}/${room.id}`, { method: 'PUT', headers: {'Content-Type':'application/json', Accept: 'application/json'}, body: JSON.stringify(updatedRoom) });
                                        if (!uRes.ok) throw new Error('Failed to update room');
                                    }
                                    msg.textContent = `Finished room ${roomNumber}`;
                                    loadMaintenanceRooms();
                                    loadAssignedTasks();
                                } catch (err) {
                                    msg.textContent = err.message;
                                }
                            });
                        });
                    }

                    loadMaintenanceRooms();
                    loadAssignedTasks();
                    // Poll every 10s to refresh lists (auto-exclude cleaned/assigned rooms)
                    setInterval(() => {
                        if (!refreshPaused && !isEditing()) {
                            loadMaintenanceRooms();
                            loadAssignedTasks();
                        }
                    }, 10000);
                </script>
                </body>
                </html>
                """;
        return ResponseEntity.ok().contentType(MediaType.TEXT_HTML).body(html);
    }

    @GetMapping("/rooms/**")
    public ResponseEntity<String> proxyRooms(RequestEntity<String> request) {
        return proxy(request.getMethod(), "room-service", request.getUrl().getPath(), request.getHeaders(), request.getBody());
    }

    @PostMapping("/rooms/**")
    public ResponseEntity<String> proxyRoomsPost(RequestEntity<String> request) {
        return proxy(request.getMethod(), "room-service", request.getUrl().getPath(), request.getHeaders(), request.getBody());
    }

    @PutMapping("/rooms/**")
    public ResponseEntity<String> proxyRoomsPut(RequestEntity<String> request) {
        return proxy(request.getMethod(), "room-service", request.getUrl().getPath(), request.getHeaders(), request.getBody());
    }

    @PatchMapping("/rooms/**")
    public ResponseEntity<String> proxyRoomsPatch(RequestEntity<String> request) {
        return proxy(request.getMethod(), "room-service", request.getUrl().getPath(), request.getHeaders(), request.getBody());
    }

    @GetMapping("/housekeeping/**")
    public ResponseEntity<String> proxyHousekeeping(RequestEntity<String> request) {
        return proxy(request.getMethod(), "housekeeping-service", request.getUrl().getPath(), request.getHeaders(), request.getBody());
    }

    @PostMapping("/housekeeping/**")
    public ResponseEntity<String> proxyHousekeepingPost(RequestEntity<String> request) {
        return proxy(request.getMethod(), "housekeeping-service", request.getUrl().getPath(), request.getHeaders(), request.getBody());
    }

    @PutMapping("/housekeeping/**")
    public ResponseEntity<String> proxyHousekeepingPut(RequestEntity<String> request) {
        return proxy(request.getMethod(), "housekeeping-service", request.getUrl().getPath(), request.getHeaders(), request.getBody());
    }

    // Guests are now sourced from the rooms table via the room-service.
    private final ObjectMapper objectMapper = new ObjectMapper();

    @GetMapping(value = {"/guests", "/guests/"}, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> listGuests() {
        try {
            ResponseEntity<String> roomsRes = proxy(HttpMethod.GET, "room-service", "/api/rooms", new org.springframework.http.HttpHeaders(), null);
            if (!roomsRes.getStatusCode().is2xxSuccessful()) return roomsRes;
            JsonNode rooms = objectMapper.readTree(roomsRes.getBody());
            ArrayNode guests = objectMapper.createArrayNode();
            if (rooms.isArray()) {
                for (JsonNode r : rooms) {
                    String guest = r.path("guestName").asText(null);
                    if (guest != null && !guest.isEmpty()) {
                        ObjectNode g = objectMapper.createObjectNode();
                        g.put("id", r.path("id").asLong());
                        g.put("name", guest);
                        g.put("roomNumber", r.path("number").asText());
                        guests.add(g);
                    }
                }
            }
            return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(objectMapper.writeValueAsString(guests));
        } catch (Exception e) {
            return ResponseEntity.status(org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping(value = {"/guests/{id}", "/guests/{id}/"}, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getGuest(@PathVariable Long id) {
        try {
            ResponseEntity<String> roomRes = proxy(HttpMethod.GET, "room-service", "/api/rooms/" + id, new org.springframework.http.HttpHeaders(), null);
            if (!roomRes.getStatusCode().is2xxSuccessful()) return roomRes;
            JsonNode r = objectMapper.readTree(roomRes.getBody());
            String guest = r.path("guestName").asText(null);
            if (guest == null || guest.isEmpty()) return ResponseEntity.notFound().build();
            ObjectNode g = objectMapper.createObjectNode();
            g.put("id", r.path("id").asLong());
            g.put("name", guest);
            g.put("roomNumber", r.path("number").asText());
            return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(objectMapper.writeValueAsString(g));
        } catch (Exception e) {
            return ResponseEntity.status(org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PutMapping(value = {"/guests/{id}", "/guests/{id}/"}, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> updateGuest(@PathVariable Long id, @RequestBody String body) {
        try {
            // body expected to contain { "name": "..." }
            JsonNode payload = objectMapper.readTree(body);
            String name = payload.path("name").asText(null);
            if (name == null) return ResponseEntity.badRequest().body("Missing name");
            // Fetch room
            ResponseEntity<String> roomRes = proxy(HttpMethod.GET, "room-service", "/api/rooms/" + id, new org.springframework.http.HttpHeaders(), null);
            if (!roomRes.getStatusCode().is2xxSuccessful()) return roomRes;
            JsonNode room = objectMapper.readTree(roomRes.getBody());
            // Build updated room payload
            ObjectNode updated = objectMapper.createObjectNode();
            updated.put("number", room.path("number").asText());
            updated.put("type", room.path("type").asText());
            updated.putPOJO("price", room.path("price").decimalValue());
            updated.put("status", room.path("status").asText());
            updated.put("guestName", name);
            org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            ResponseEntity<String> putRes = proxy(HttpMethod.PUT, "room-service", "/api/rooms/" + id, headers, objectMapper.writeValueAsString(updated));
            return putRes;
        } catch (Exception e) {
            return ResponseEntity.status(org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}
