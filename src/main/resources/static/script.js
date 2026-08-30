const API = '/api';

let currentSelectedRole = '';
let currentUser = null; // { id, name, username, role }

// ---------- helpers ----------

function esc(str) {
    const div = document.createElement('div');
    div.textContent = str ?? '';
    return div.innerHTML;
}

async function apiCall(path, options = {}) {
    const res = await fetch(API + path, {
        headers: { 'Content-Type': 'application/json' },
        ...options,
    });
    const body = await res.json().catch(() => ({ success: false, message: 'Unexpected server response' }));
    if (!res.ok || body.success === false) {
        throw new Error(body.message || 'Request failed');
    }
    return body.data;
}

function showError(elId, message) {
    const el = document.getElementById(elId);
    el.textContent = message;
    el.classList.remove('hidden');
}

function hideError(elId) {
    document.getElementById(elId).classList.add('hidden');
}

const STATUS_LABEL = {
    PENDING: 'Pending',
    IN_PROGRESS: 'In Progress',
    COMPLETED: 'Completed',
};

// ---------- role selection / auth screens ----------

function selectRole(role) {
    currentSelectedRole = role;
    hideError('loginError');
    document.getElementById('loginUser').value = '';
    document.getElementById('loginPass').value = '';

    if (role === 'ADMIN') {
        document.getElementById('loginTitle').innerText = 'Admin Portal';
        document.getElementById('loginHint').innerText = 'Try username: admin | password: 1234';
    } else {
        document.getElementById('loginTitle').innerText = 'Employee Portal';
        document.getElementById('loginHint').innerText = 'Try username: employee | password: abcd';
    }

    document.getElementById('roleSelection').classList.add('hidden');
    document.getElementById('authScreen').classList.remove('hidden');
}

function goBackToRoles() {
    document.getElementById('authScreen').classList.add('hidden');
    document.getElementById('roleSelection').classList.remove('hidden');
    document.getElementById('loginUser').value = '';
    document.getElementById('loginPass').value = '';
}

async function handleLogin() {
    const username = document.getElementById('loginUser').value.trim();
    const password = document.getElementById('loginPass').value;
    hideError('loginError');

    if (!username || !password) {
        showError('loginError', 'Please enter both username and password.');
        return;
    }

    const btn = document.getElementById('loginBtn');
    btn.disabled = true;
    btn.textContent = 'Signing in...';

    try {
        const user = await apiCall('/auth/login', {
            method: 'POST',
            body: JSON.stringify({ username, password, role: currentSelectedRole }),
        });
        currentUser = user;
        document.getElementById('authScreen').classList.add('hidden');

        if (user.role === 'ADMIN') {
            document.getElementById('adminName').textContent = user.name;
            document.getElementById('adminDashboard').classList.remove('hidden');
            await loadAdminDashboard();
        } else {
            document.getElementById('employeeName').textContent = user.name;
            document.getElementById('empAvatar').textContent = user.name.charAt(0).toUpperCase();
            document.getElementById('employeeDashboard').classList.remove('hidden');
            await loadEmployeeTasks();
        }
    } catch (err) {
        showError('loginError', err.message);
    } finally {
        btn.disabled = false;
        btn.textContent = 'Secure Login';
    }
}

function handleLogout() {
    currentUser = null;
    document.getElementById('adminDashboard').classList.add('hidden');
    document.getElementById('employeeDashboard').classList.add('hidden');
    document.getElementById('loginUser').value = '';
    document.getElementById('loginPass').value = '';
    document.getElementById('roleSelection').classList.remove('hidden');
}

// ---------- admin dashboard ----------

async function loadAdminDashboard() {
    await Promise.all([loadEmployeeOptions(), loadAllTasks()]);
}

async function loadEmployeeOptions() {
    const select = document.getElementById('empSelect');
    try {
        const employees = await apiCall('/employees');
        select.innerHTML = employees.length
            ? employees.map(e => `<option value="${esc(e.username)}">${esc(e.name)} (${esc(e.username)})</option>`).join('')
            : '<option value="">No employees yet</option>';
    } catch (err) {
        select.innerHTML = '<option value="">Failed to load employees</option>';
    }
}

async function loadAllTasks() {
    const list = document.getElementById('adminTaskList');
    try {
        const tasks = await apiCall('/tasks');
        document.getElementById('taskCount').textContent = tasks.length;
        list.innerHTML = tasks.length
            ? tasks.map(renderAdminTask).join('')
            : '<li class="empty-state">No tasks assigned yet.</li>';
    } catch (err) {
        list.innerHTML = `<li class="empty-state">Failed to load tasks: ${esc(err.message)}</li>`;
    }
}

function renderAdminTask(t) {
    return `
        <li>
            <div class="task-info">
                <strong>${esc(t.employeeName)} <span class="muted small">(${esc(t.employeeUsername)})</span></strong>
                <p>${esc(t.description)}</p>
                <div class="task-meta">Assigned by ${esc(t.assignedBy)}</div>
            </div>
            <div class="task-actions">
                <span class="status-tag status-${t.status}">${STATUS_LABEL[t.status]}</span>
                <button class="icon-btn" onclick="deleteTask(${t.id})">🗑 Delete</button>
            </div>
        </li>`;
}

async function createEmployee() {
    const name = document.getElementById('newEmpName').value.trim();
    const username = document.getElementById('newEmpUsername').value.trim();
    const password = document.getElementById('newEmpPassword').value;
    hideError('empError');

    if (!name || !username || !password) {
        showError('empError', 'Name, username and password are all required.');
        return;
    }

    try {
        await apiCall('/employees', {
            method: 'POST',
            body: JSON.stringify({ name, username, password }),
        });
        document.getElementById('newEmpName').value = '';
        document.getElementById('newEmpUsername').value = '';
        document.getElementById('newEmpPassword').value = '';
        await loadEmployeeOptions();
    } catch (err) {
        showError('empError', err.message);
    }
}

async function assignTask() {
    const employeeUsername = document.getElementById('empSelect').value;
    const description = document.getElementById('taskTitle').value.trim();
    hideError('taskError');

    if (!employeeUsername) {
        showError('taskError', 'Add an employee first, then pick one to assign to.');
        return;
    }
    if (!description) {
        showError('taskError', 'Please describe the task.');
        return;
    }

    try {
        await apiCall('/tasks', {
            method: 'POST',
            body: JSON.stringify({ employeeUsername, description, assignedBy: currentUser.name }),
        });
        document.getElementById('taskTitle').value = '';
        await loadAllTasks();
    } catch (err) {
        showError('taskError', err.message);
    }
}

async function deleteTask(id) {
    try {
        await apiCall(`/tasks/${id}`, { method: 'DELETE' });
        await loadAllTasks();
    } catch (err) {
        alert(err.message);
    }
}

// ---------- employee dashboard ----------

async function loadEmployeeTasks() {
    const list = document.getElementById('employeeTaskList');
    try {
        const tasks = await apiCall(`/tasks/employee/${encodeURIComponent(currentUser.username)}`);
        list.innerHTML = tasks.length
            ? tasks.map(renderEmployeeTask).join('')
            : '<li class="empty-state">You have no tasks yet. 🎉</li>';
    } catch (err) {
        list.innerHTML = `<li class="empty-state">Failed to load tasks: ${esc(err.message)}</li>`;
    }
}

function renderEmployeeTask(t) {
    return `
        <li>
            <div class="task-info">
                <strong>From: ${esc(t.assignedBy)}</strong>
                <p>${esc(t.description)}</p>
            </div>
            <div class="task-actions">
                <select class="status-select" onchange="updateStatus(${t.id}, this.value)">
                    ${Object.keys(STATUS_LABEL).map(s =>
                        `<option value="${s}" ${s === t.status ? 'selected' : ''}>${STATUS_LABEL[s]}</option>`
                    ).join('')}
                </select>
                <span class="status-tag status-${t.status}">${STATUS_LABEL[t.status]}</span>
            </div>
        </li>`;
}

async function updateStatus(id, status) {
    try {
        await apiCall(`/tasks/${id}/status`, {
            method: 'PATCH',
            body: JSON.stringify({ status }),
        });
        await loadEmployeeTasks();
    } catch (err) {
        alert(err.message);
    }
}
