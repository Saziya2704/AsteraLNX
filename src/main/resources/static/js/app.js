/* AsteraLNX operations console */
const user = getUser();
if (!getToken() || !user.role) window.location.href = '/index.html';

/** Module permissions: r = view list, w = create/edit/actions */
const PERMS = {
  ADMIN: {
    users: 'w', customers: 'w', inquiries: 'w', quotes: 'w', orders: 'w', bookings: 'w',
    shipments: 'w', tracking: 'w', warehouses: 'w', commodities: 'w', receipts: 'w',
    stockTransfers: 'w', drivers: 'w', vehicles: 'w', trips: 'w', invoices: 'w', reports: 'w'
  },
  OPERATIONS_MANAGER: {
    customers: 'w', inquiries: 'w', quotes: 'w', orders: 'w', bookings: 'w',
    shipments: 'w', tracking: 'w', warehouses: 'r', commodities: 'r', receipts: 'r',
    stockTransfers: 'r', drivers: 'w', vehicles: 'w', trips: 'w', invoices: 'w'
  },
  WAREHOUSE_MANAGER: {
    customers: 'r', inquiries: 'r', quotes: 'r', orders: 'r',
    warehouses: 'w', commodities: 'w', receipts: 'w', stockTransfers: 'w'
  },
  DRIVER: {
    myDeliveries: 'r', myTrips: 'r'
  }
};

function can(module, need) {
  const p = (PERMS[user.role] || {})[module];
  if (!p) return false;
  if (need === 'r') return p === 'r' || p === 'w';
  return p === 'w';
}

const MENUS = {
  ADMIN: [
    { section: 'Overview' },
    { id: 'dashboard', label: 'Dashboard', icon: 'bi-speedometer2', hash: '#/dashboard' },
    { section: 'Commercial' },
    { id: 'customers', label: 'Customers', icon: 'bi-people', hash: '#/customers' },
    { id: 'inquiries', label: 'Inquiries', icon: 'bi-chat-left-text', hash: '#/inquiries' },
    { id: 'quotes', label: 'Quotes', icon: 'bi-file-earmark-text', hash: '#/quotes' },
    { id: 'orders', label: 'Orders', icon: 'bi-clipboard-check', hash: '#/orders' },
    { section: 'Operations' },
    { id: 'bookings', label: 'Bookings', icon: 'bi-calendar-check', hash: '#/bookings' },
    { id: 'shipments', label: 'Shipments', icon: 'bi-box-seam', hash: '#/shipments' },
    { id: 'tracking', label: 'Tracking', icon: 'bi-geo-alt', hash: '#/tracking' },
    { id: 'trips', label: 'Trips', icon: 'bi-signpost-2', hash: '#/trips' },
    { section: 'Warehouse' },
    { id: 'warehouses', label: 'Warehouses', icon: 'bi-building', hash: '#/warehouses' },
    { id: 'commodities', label: 'Commodities', icon: 'bi-boxes', hash: '#/commodities' },
    { id: 'receipts', label: 'Receipts', icon: 'bi-receipt', hash: '#/receipts' },
    { id: 'stock-transfers', label: 'Stock Transfers', icon: 'bi-arrow-left-right', hash: '#/stock-transfers' },
    { section: 'Fleet & Finance' },
    { id: 'drivers', label: 'Drivers', icon: 'bi-person-badge', hash: '#/drivers' },
    { id: 'vehicles', label: 'Vehicles', icon: 'bi-truck', hash: '#/vehicles' },
    { id: 'invoices', label: 'Invoices', icon: 'bi-cash-stack', hash: '#/invoices' },
    { id: 'reports', label: 'Reports', icon: 'bi-file-earmark-bar-graph', hash: '#/reports' },
    { section: 'Admin' },
    { id: 'users', label: 'Users', icon: 'bi-shield-lock', hash: '#/users' }
  ],
  OPERATIONS_MANAGER: [
    { section: 'Overview' },
    { id: 'dashboard', label: 'Dashboard', icon: 'bi-speedometer2', hash: '#/dashboard' },
    { section: 'Work' },
    { id: 'customers', label: 'Customers', icon: 'bi-people', hash: '#/customers' },
    { id: 'inquiries', label: 'Inquiries', icon: 'bi-chat-left-text', hash: '#/inquiries' },
    { id: 'quotes', label: 'Quotes', icon: 'bi-file-earmark-text', hash: '#/quotes' },
    { id: 'orders', label: 'Orders', icon: 'bi-clipboard-check', hash: '#/orders' },
    { id: 'bookings', label: 'Bookings', icon: 'bi-calendar-check', hash: '#/bookings' },
    { id: 'shipments', label: 'Shipments', icon: 'bi-box-seam', hash: '#/shipments' },
    { id: 'tracking', label: 'Tracking', icon: 'bi-geo-alt', hash: '#/tracking' },
    { id: 'trips', label: 'Trips', icon: 'bi-signpost-2', hash: '#/trips' },
    { id: 'drivers', label: 'Drivers', icon: 'bi-person-badge', hash: '#/drivers' },
    { id: 'vehicles', label: 'Vehicles', icon: 'bi-truck', hash: '#/vehicles' },
    { id: 'invoices', label: 'Invoices', icon: 'bi-cash-stack', hash: '#/invoices' }
  ],
  WAREHOUSE_MANAGER: [
    { section: 'Overview' },
    { id: 'dashboard', label: 'Dashboard', icon: 'bi-speedometer2', hash: '#/dashboard' },
    { section: 'Reference' },
    { id: 'customers', label: 'Customers', icon: 'bi-people', hash: '#/customers' },
    { id: 'orders', label: 'Orders', icon: 'bi-clipboard-check', hash: '#/orders' },
    { section: 'Warehouse' },
    { id: 'warehouses', label: 'Warehouses', icon: 'bi-building', hash: '#/warehouses' },
    { id: 'commodities', label: 'Commodities', icon: 'bi-boxes', hash: '#/commodities' },
    { id: 'receipts', label: 'Receipts', icon: 'bi-receipt', hash: '#/receipts' },
    { id: 'stock-transfers', label: 'Stock Transfers', icon: 'bi-arrow-left-right', hash: '#/stock-transfers' }
  ],
  DRIVER: [
    { section: 'My work' },
    { id: 'dashboard', label: 'Dashboard', icon: 'bi-speedometer2', hash: '#/dashboard' },
    { id: 'my-deliveries', label: 'My Deliveries', icon: 'bi-box-seam', hash: '#/my-deliveries' },
    { id: 'my-trips', label: 'My Trips', icon: 'bi-signpost-2', hash: '#/my-trips' }
  ]
};

function statusBadge(s) {
  if (s == null || s === '') return '—';
  const u = String(s).toUpperCase();
  let cls = 'st-muted';
  if (/ACTIVE|DELIVERED|COMPLETED|PAID|ACCEPTED|CONFIRMED/.test(u)) cls = 'st-success';
  else if (/CANCEL|INACTIVE|REJECTED/.test(u)) cls = 'st-danger';
  else if (/DELAY|OVERDUE|PENDING|WARN/.test(u)) cls = 'st-warn';
  else if (/TRANSIT|PROGRESS|PROCESS|SHIPPED|ASSIGN|SENT|ISSUED/.test(u)) cls = 'st-info';
  else if (/DRAFT|CREATED|READY/.test(u)) cls = 'st-primary';
  return `<span class="badge-st ${cls}">${String(s).replace(/_/g, ' ')}</span>`;
}

function fmtDate(d) {
  if (!d) return '—';
  const x = new Date(d);
  if (isNaN(x)) return String(d).slice(0, 10);
  return x.toLocaleDateString('en-IN', { day: '2-digit', month: 'short', year: 'numeric' });
}
function fmtMoney(n) {
  if (n == null || n === '') return '—';
  return '₹' + Number(n).toLocaleString('en-IN', { minimumFractionDigits: 2, maximumFractionDigits: 2 });
}
function pageHead(title, sub, actionsHtml) {
  return `<div class="page-head"><div><h1>${title}</h1>${sub ? `<p class="sub">${sub}</p>` : ''}</div>
    <div class="btn-row">${actionsHtml || ''}</div></div>`;
}
function unwrapList(res) {
  if (!res) return [];
  if (Array.isArray(res)) return res;
  if (Array.isArray(res.content)) return res.content;
  if (Array.isArray(res.data)) return res.data;
  if (Array.isArray(res.items)) return res.items;
  return [];
}
function escapeHtml(s) {
  return String(s ?? '').replace(/&/g,'&amp;').replace(/</g,'&lt;').replace(/>/g,'&gt;').replace(/"/g,'&quot;');
}

/** Interactive data table: search, filters, sort, pagination — focus-safe (no input remount on type) */
const TableState = {};

function mountTable(containerId, opts) {
  const id = containerId;
  TableState[id] = {
    rows: opts.rows || [],
    columns: opts.columns,
    page: 1,
    pageSize: opts.pageSize || 10,
    search: '',
    statusFilter: '',
    dateFrom: '',
    dateTo: '',
    sortKey: opts.defaultSort || null,
    sortDir: 'asc',
    statusField: opts.statusField || 'status',
    dateField: opts.dateField || null,
    searchKeys: opts.searchKeys || [],
    rowActions: opts.rowActions || null,
    emptyText: opts.emptyText || 'No records found',
    shellReady: false
  };
  const host = document.getElementById(id);
  if (!host) return;
  host.innerHTML = buildTableShell(id);
  bindTableChrome(id);
  TableState[id].shellReady = true;
  updateTableBody(id);
}

function filteredRows(st) {
  let rows = st.rows.slice();
  const q = st.search.trim().toLowerCase();
  if (q) {
    rows = rows.filter(r => {
      if (st.searchKeys.length) {
        return st.searchKeys.some(k => String(r[k] ?? '').toLowerCase().includes(q));
      }
      return JSON.stringify(r).toLowerCase().includes(q);
    });
  }
  if (st.statusFilter) {
    rows = rows.filter(r => String(r[st.statusField] ?? '').toUpperCase() === st.statusFilter.toUpperCase());
  }
  if (st.dateField && (st.dateFrom || st.dateTo)) {
    rows = rows.filter(r => {
      const raw = r[st.dateField];
      if (!raw) return false;
      const d = new Date(raw);
      if (isNaN(d)) return false;
      if (st.dateFrom && d < new Date(st.dateFrom)) return false;
      if (st.dateTo) {
        const to = new Date(st.dateTo);
        to.setHours(23, 59, 59, 999);
        if (d > to) return false;
      }
      return true;
    });
  }
  if (st.sortKey) {
    const k = st.sortKey;
    const dir = st.sortDir === 'desc' ? -1 : 1;
    rows.sort((a, b) => {
      let va = a[k], vb = b[k];
      if (va == null) va = '';
      if (vb == null) vb = '';
      if (typeof va === 'number' && typeof vb === 'number') return (va - vb) * dir;
      return String(va).localeCompare(String(vb), undefined, { numeric: true }) * dir;
    });
  }
  return rows;
}

function buildTableShell(id) {
  const st = TableState[id];
  const statuses = [...new Set(st.rows.map(r => r[st.statusField]).filter(v => v !== undefined && v !== null && v !== ''))].map(String).sort();
  return `
    <div class="work-panel">
      <div class="work-toolbar" id="${id}_toolbar">
        <input type="search" class="form-control form-control-sm search-box" id="${id}_q" placeholder="Search…" autocomplete="off">
        ${statuses.length ? `<label class="tb-label">Status</label>
          <select class="form-select form-select-sm filter-box" id="${id}_st">
            <option value="">All</option>
            ${statuses.map(s => `<option value="${escapeHtml(s)}">${escapeHtml(String(s).replace(/_/g,' '))}</option>`).join('')}
          </select>` : ''}
        ${st.dateField ? `
          <label class="tb-label">From</label>
          <input type="date" class="form-control form-control-sm filter-box" id="${id}_df">
          <label class="tb-label">To</label>
          <input type="date" class="form-control form-control-sm filter-box" id="${id}_dt">
          <select class="form-select form-select-sm filter-box" id="${id}_preset" title="Date preset">
            <option value="">Date range</option>
            <option value="today">Today</option>
            <option value="7">Last 7 days</option>
            <option value="30">Last 30 days</option>
            <option value="month">This month</option>
          </select>
        ` : ''}
        <button type="button" class="btn btn-outline-secondary btn-sm" id="${id}_reset">Reset</button>
      </div>
      <div class="work-body">
        <table class="work-table">
          <thead id="${id}_thead"></thead>
          <tbody id="${id}_tbody"></tbody>
        </table>
      </div>
      <div class="work-foot" id="${id}_foot"></div>
    </div>`;
}

function bindTableChrome(id) {
  const st = TableState[id];
  const qEl = document.getElementById(id + '_q');
  let tmr;
  qEl?.addEventListener('input', () => {
    clearTimeout(tmr);
    const val = qEl.value;
    tmr = setTimeout(() => {
      st.search = val;
      st.page = 1;
      updateTableBody(id);
    }, 200);
  });
  document.getElementById(id + '_st')?.addEventListener('change', e => {
    st.statusFilter = e.target.value; st.page = 1; updateTableBody(id);
  });
  document.getElementById(id + '_df')?.addEventListener('change', e => {
    st.dateFrom = e.target.value; st.page = 1; updateTableBody(id);
  });
  document.getElementById(id + '_dt')?.addEventListener('change', e => {
    st.dateTo = e.target.value; st.page = 1; updateTableBody(id);
  });
  document.getElementById(id + '_preset')?.addEventListener('change', e => {
    const v = e.target.value;
    const df = document.getElementById(id + '_df');
    const dt = document.getElementById(id + '_dt');
    const today = new Date();
    const iso = d => d.toISOString().slice(0, 10);
    if (!v) return;
    let from = new Date(today), to = new Date(today);
    if (v === 'today') { /* same day */ }
    else if (v === '7') { from.setDate(from.getDate() - 6); }
    else if (v === '30') { from.setDate(from.getDate() - 29); }
    else if (v === 'month') { from = new Date(today.getFullYear(), today.getMonth(), 1); }
    st.dateFrom = iso(from);
    st.dateTo = iso(to);
    if (df) df.value = st.dateFrom;
    if (dt) dt.value = st.dateTo;
    st.page = 1;
    updateTableBody(id);
  });
  document.getElementById(id + '_reset')?.addEventListener('click', () => {
    st.search = ''; st.statusFilter = ''; st.dateFrom = ''; st.dateTo = ''; st.page = 1;
    const q = document.getElementById(id + '_q'); if (q) q.value = '';
    const s = document.getElementById(id + '_st'); if (s) s.value = '';
    const df = document.getElementById(id + '_df'); if (df) df.value = '';
    const dt = document.getElementById(id + '_dt'); if (dt) dt.value = '';
    const pr = document.getElementById(id + '_preset'); if (pr) pr.value = '';
    updateTableBody(id);
  });
}

function updateTableBody(id) {
  const st = TableState[id];
  if (!st) return;
  const all = filteredRows(st);
  const total = all.length;
  const pages = Math.max(1, Math.ceil(total / st.pageSize));
  if (st.page > pages) st.page = pages;
  const start = (st.page - 1) * st.pageSize;
  const pageRows = all.slice(start, start + st.pageSize);

  const thead = document.getElementById(id + '_thead');
  const tbody = document.getElementById(id + '_tbody');
  const foot = document.getElementById(id + '_foot');
  if (!thead || !tbody || !foot) return;

  thead.innerHTML = '<tr>' + st.columns.map(c => {
    const sorted = st.sortKey === c.key;
    return `<th class="${c.sortable === false ? '' : 'sortable'} ${sorted ? 'sorted' : ''}" data-sort="${c.key || ''}">${c.label}${c.sortable === false ? '' : '<span class="sort-ind">' + (sorted ? (st.sortDir === 'asc' ? '▲' : '▼') : '⇅') + '</span>'}</th>`;
  }).join('') + (st.rowActions ? '<th>Actions</th>' : '') + '</tr>';

  thead.querySelectorAll('th.sortable').forEach(th => {
    th.addEventListener('click', () => {
      const k = th.getAttribute('data-sort');
      if (!k) return;
      if (st.sortKey === k) st.sortDir = st.sortDir === 'asc' ? 'desc' : 'asc';
      else { st.sortKey = k; st.sortDir = 'asc'; }
      updateTableBody(id);
    });
  });

  if (!pageRows.length) {
    tbody.innerHTML = `<tr><td colspan="${st.columns.length + (st.rowActions ? 1 : 0)}"><div class="empty-box"><i class="bi bi-inbox"></i>${escapeHtml(st.emptyText)}</div></td></tr>`;
  } else {
    tbody.innerHTML = pageRows.map(r => {
      const cells = st.columns.map(c => `<td>${c.render ? c.render(r) : escapeHtml(r[c.key] ?? '—')}</td>`).join('');
      const acts = st.rowActions ? `<td><div class="btn-row">${st.rowActions(r) || ''}</div></td>` : '';
      return `<tr>${cells}${acts}</tr>`;
    }).join('');
  }

  foot.innerHTML = `
    <div>
      Show
      <select class="form-select form-select-sm" id="${id}_ps">
        ${[10, 25, 50, 100].map(n => `<option value="${n}" ${st.pageSize === n ? 'selected' : ''}>${n}</option>`).join('')}
      </select>
      entries
      <span class="ms-2">Showing ${total ? start + 1 : 0}–${Math.min(start + st.pageSize, total)} of ${total}</span>
    </div>
    <div class="pager">
      <button type="button" class="btn btn-outline-secondary btn-sm" data-pg="prev" ${st.page <= 1 ? 'disabled' : ''}>Prev</button>
      <span>Page ${st.page} / ${pages}</span>
      <button type="button" class="btn btn-outline-secondary btn-sm" data-pg="next" ${st.page >= pages ? 'disabled' : ''}>Next</button>
    </div>`;

  document.getElementById(id + '_ps')?.addEventListener('change', e => {
    st.pageSize = Number(e.target.value) || 10; st.page = 1; updateTableBody(id);
  });
  foot.querySelectorAll('[data-pg]').forEach(btn => {
    btn.addEventListener('click', () => {
      if (btn.getAttribute('data-pg') === 'prev' && st.page > 1) st.page--;
      if (btn.getAttribute('data-pg') === 'next' && st.page < pages) st.page++;
      updateTableBody(id);
    });
  });
}

async function loadList(path) {
  const res = await apiGet(path.includes('?') ? path : path + '?page=0&size=200');
  return unwrapList(res);
}

function buildSidebar() {
  const menu = MENUS[user.role] || MENUS.DRIVER;
  let html = '';
  menu.forEach(item => {
    if (item.section) html += `<div class="nav-section">${item.section}</div>`;
    else html += `<a href="${item.hash}" class="nav-link-app" data-page="${item.id}" title="${item.label}"><i class="bi ${item.icon}"></i><span>${item.label}</span></a>`;
  });
  document.getElementById('sidebarNav').innerHTML = html;
  const name = user.fullName || user.email || 'User';
  const initial = name.trim().charAt(0).toUpperCase();
  const set = (id, val) => { const el = document.getElementById(id); if (el) el.textContent = val; };
  set('userName', name);
  set('userRole', (user.role || '').replace(/_/g, ' '));
  set('userAvatar', initial);
  set('topAvatar', initial);
  set('topUserName', name);
  set('headerRoleBadge', (user.role || '').replace(/_/g, ' '));
  const un = document.getElementById('userName');
  if (un) un.title = name;
}

const routes = {};
function route(h, fn) { routes[h] = fn; }

async function navigate() {
  const hash = window.location.hash || '#/dashboard';
  const page = hash.replace('#/', '').split('?')[0];
  document.querySelectorAll('.nav-link-app').forEach(a => a.classList.toggle('active', a.dataset.page === page));
  const titles = {
    dashboard: 'Dashboard', customers: 'Customers', inquiries: 'Inquiries', quotes: 'Quotes',
    orders: 'Orders', bookings: 'Bookings', shipments: 'Shipments', tracking: 'Tracking',
    warehouses: 'Warehouses', commodities: 'Commodities', receipts: 'Receipts',
    drivers: 'Drivers', vehicles: 'Vehicles', trips: 'Trips', invoices: 'Invoices',
    reports: 'Reports', users: 'Users', 'stock-transfers': 'Stock Transfers',
    'my-deliveries': 'My Deliveries', 'my-trips': 'My Trips'
  };
  document.getElementById('headerTitle').textContent = titles[page] || 'AsteraLNX';
  const bc = document.getElementById('breadcrumb');
  if (bc) bc.innerHTML = `<li class="breadcrumb-item">Home</li><li class="breadcrumb-item active">${titles[page] || page}</li>`;
  const content = document.getElementById('pageContent');
  content.innerHTML = '<div class="work-panel p-3"><div class="skeleton skeleton-row"></div><div class="skeleton skeleton-row"></div><div class="skeleton skeleton-row"></div><div class="skeleton skeleton-row"></div></div>';
  try {
    const handler = routes[hash.split('?')[0]] || routes['#/dashboard'];
    await handler(content);
  } catch (err) {
    content.innerHTML = `<div class="alert alert-danger">${escapeHtml(err.message || 'Failed to load')}</div>`;
  }
}

/* —— Dashboard —— */
route('#/dashboard', async (el) => {
  let stats = {};
  try { stats = await apiGet('/dashboard') || {}; } catch (e) { stats = {}; }
  const role = user.role;
  const cards = [];
  const push = (lbl, val, meta) => cards.push({ lbl, val: val ?? '—', meta: meta || '' });

  if (role === 'ADMIN' || role === 'OPERATIONS_MANAGER') {
    push('Orders', stats.orders, (stats.ordersReadyForShipment != null ? stats.ordersReadyForShipment + ' ready' : ''));
    push('In transit', stats.shipmentsInTransit);
    push('Delayed', stats.delayedShipments);
    push('Delivered', stats.ordersDelivered);
    push('Invoices overdue', stats.invoicesOverdue);
    push('Low stock', stats.lowStockItems);
    if (role === 'ADMIN') {
      push('Customers', stats.customers);
      push('Warehouses', stats.warehouses);
    }
  } else if (role === 'WAREHOUSE_MANAGER') {
    push('Warehouses', stats.warehouses);
    push('Ready to ship', stats.ordersReadyForShipment);
    push('Shipments created', stats.shipmentsCreated);
    push('Low stock', stats.lowStockItems);
  } else {
    push('My shipments', stats.assignedShipments);
    push('My trips', stats.myTrips);
  }

  const queueTable = (title, rows, cols) => {
    if (!rows || !rows.length) {
      return `<div class="work-panel p-3 mb-3"><div class="fw-semibold mb-2">${title}</div><div class="text-muted" style="font-size:12.5px">No items</div></div>`;
    }
    const head = cols.map(c => `<th>${c.label}</th>`).join('');
    const body = rows.map(r => '<tr>' + cols.map(c => `<td>${c.render ? c.render(r) : escapeHtml(r[c.key] ?? '—')}</td>`).join('') + '</tr>').join('');
    return `<div class="work-panel mb-3"><div class="p-2 px-3 border-bottom fw-semibold" style="font-size:13px;background:#f7f9fb">${title}</div>
      <div class="work-body"><table class="work-table"><thead><tr>${head}</tr></thead><tbody>${body}</tbody></table></div></div>`;
  };

  let charts = '';
  let queues = '';
  if (role === 'ADMIN' || role === 'OPERATIONS_MANAGER') {
    charts = `<div class="row g-3 mb-3">
      <div class="col-md-6"><div class="work-panel p-3"><div class="fw-semibold mb-2">Order status mix</div><div style="height:200px"><canvas id="chartOrders"></canvas></div></div></div>
      <div class="col-md-6"><div class="work-panel p-3"><div class="fw-semibold mb-2">Shipment status mix</div><div style="height:200px"><canvas id="chartShipments"></canvas></div></div></div>
    </div>`;
    queues = queueTable('Ready for shipment', stats.readyForShipment, [
      { key: 'orderNumber', label: 'Order #' },
      { key: 'origin', label: 'Origin' },
      { key: 'destination', label: 'Destination' },
      { key: 'status', label: 'Status', render: r => statusBadge(r.status) }
    ]) + queueTable('In transit / delayed', [...(stats.inTransitShipments || []), ...(stats.delayedShipmentList || [])].slice(0, 10), [
      { key: 'shipmentNumber', label: 'Shipment #' },
      { key: 'origin', label: 'Origin' },
      { key: 'destination', label: 'Destination' },
      { key: 'status', label: 'Status', render: r => statusBadge(r.status) }
    ]) + queueTable('Recent orders', stats.recentOrders, [
      { key: 'orderNumber', label: 'Order #' },
      { key: 'status', label: 'Status', render: r => statusBadge(r.status) },
      { key: 'origin', label: 'Origin' },
      { key: 'destination', label: 'Destination' }
    ]) + queueTable('Low stock (qty < 10)', stats.lowStockList, [
      { key: 'commodityCode', label: 'Code' },
      { key: 'name', label: 'Name' },
      { key: 'quantity', label: 'Qty' },
      { key: 'unit', label: 'Unit' }
    ]);
  } else if (role === 'WAREHOUSE_MANAGER') {
    queues = queueTable('Ready for shipment', stats.readyForShipment, [
      { key: 'orderNumber', label: 'Order #' },
      { key: 'status', label: 'Status', render: r => statusBadge(r.status) },
      { key: 'origin', label: 'Origin' },
      { key: 'destination', label: 'Destination' }
    ]) + queueTable('Low stock (qty < 10)', stats.lowStockList, [
      { key: 'commodityCode', label: 'Code' },
      { key: 'name', label: 'Name' },
      { key: 'quantity', label: 'Qty' },
      { key: 'unit', label: 'Unit' }
    ]);
  }

  el.innerHTML = pageHead('Dashboard', `Signed in as ${escapeHtml(user.fullName || user.email)} · ${(user.role || '').replace(/_/g, ' ')}`) +
    `<div class="kpi-row">${cards.map(c => `<div class="kpi"><div class="lbl">${c.lbl}</div><div class="val">${c.val}</div>${c.meta ? `<div class="meta">${c.meta}</div>` : ''}</div>`).join('')}</div>` +
    charts + queues;

  if ((role === 'ADMIN' || role === 'OPERATIONS_MANAGER') && typeof Chart !== 'undefined') {
    setTimeout(() => {
      const mk = (canvasId, map) => {
        const ctx = document.getElementById(canvasId);
        if (!ctx || !map) return;
        const labels = Object.keys(map);
        const data = labels.map(k => map[k] || 0);
        new Chart(ctx, {
          type: 'doughnut',
          data: {
            labels: labels.map(l => l.replace(/_/g, ' ')),
            datasets: [{ data, backgroundColor: ['#2c5282','#3182ce','#4299e1','#63b3ed','#90cdf4','#bee3f8','#c53030','#b7791f'] }]
          },
          options: { plugins: { legend: { position: 'right', labels: { boxWidth: 12, font: { size: 11 } } } }, maintainAspectRatio: false }
        });
      };
      mk('chartOrders', stats.ordersByStatus);
      mk('chartShipments', stats.shipmentsByStatus);
    }, 40);
  }
});

/* Read-only record details shared by management list pages. */
window.__astViewRecords = window.__astViewRecords || Object.create(null);
window.viewRecordDetails = (key) => {
  const record = window.__astViewRecords[key];
  if (!record) { toast('Record details are no longer available. Refresh the list and try again.', 'error'); return; }
  const root = ensureModalRoot();
  const label = (key.split(':')[0] || 'Record').replace(/^\//, '').replace(/-/g, ' ');
  const fields = Object.entries(record).filter(([k,v]) => v !== null && v !== undefined && typeof v !== 'object')
    .map(([k,v]) => `<div class="col-md-6 mb-3"><div class="text-muted small">${escapeHtml(k.replace(/([A-Z])/g, ' $1').replace(/^./, c => c.toUpperCase()))}</div><div class="fw-semibold text-break">${escapeHtml(String(v))}</div></div>`).join('');
  const related = Object.entries(record).filter(([k,v]) => v && typeof v === 'object')
    .map(([k,v]) => `<div class="col-12 mb-3"><div class="text-muted small">${escapeHtml(k.replace(/([A-Z])/g, ' $1'))}</div><pre class="small bg-light p-2 rounded text-wrap">${escapeHtml(JSON.stringify(v, null, 2))}</pre></div>`).join('');
  root.innerHTML = `<div class="modal fade show d-block" tabindex="-1" style="background:rgba(0,0,0,.45)" role="dialog" aria-modal="true"><div class="modal-dialog modal-lg modal-dialog-scrollable"><div class="modal-content"><div class="modal-header"><h5 class="modal-title">${escapeHtml(label)} details</h5><button type="button" class="btn-close" aria-label="Close" onclick="document.getElementById('appModalRoot').innerHTML=''"></button></div><div class="modal-body"><div class="row">${fields}${related}</div></div><div class="modal-footer"><button type="button" class="btn btn-secondary" onclick="document.getElementById('appModalRoot').innerHTML=''">Close</button></div></div></div></div>`;
};

/* —— Generic list page helper —— */
async function listPage(el, cfg) {
  if (!can(cfg.perm, 'r')) {
    el.innerHTML = `<div class="alert alert-warning">You do not have access to this module.</div>`;
    return;
  }
  const write = can(cfg.perm, 'w');
  let rows = [];
  try {
    rows = await loadList(cfg.endpoint);
  } catch (e) {
    el.innerHTML = pageHead(cfg.title, cfg.sub) + `<div class="alert alert-danger">${escapeHtml(e.message)}</div>`;
    return;
  }
  const createBtn = write && cfg.createLabel
    ? `<button type="button" class="btn btn-primary btn-sm" id="btnCreate">${cfg.createLabel}</button>`
    : (write ? '' : `<span class="badge-st st-muted">View only</span>`);
  el.innerHTML = pageHead(cfg.title, cfg.sub, createBtn) + `<div id="tblHost"></div>`;
  if (write && cfg.onCreate) {
    document.getElementById('btnCreate')?.addEventListener('click', cfg.onCreate);
  }
  mountTable('tblHost', {
    rows,
    columns: cfg.columns,
    searchKeys: cfg.searchKeys,
    statusField: cfg.statusField || 'status',
    dateField: cfg.dateField || null,
    defaultSort: cfg.defaultSort,
    rowActions: (cfg.view === false && !(write && cfg.rowActions)) ? null : (r => {
      let actions = '';
      if (cfg.view !== false) {
        const key = `${cfg.endpoint}:${r.id ?? r.inquiryNumber ?? r.quoteNumber ?? r.orderNumber ?? r.bookingNumber ?? r.shipmentNumber ?? r.invoiceNumber ?? r.tripNumber ?? ''}`;
        window.__astViewRecords[key] = r;
        actions += `<button type="button" class="btn btn-outline-primary btn-sm" onclick="window.viewRecordDetails('${escapeHtml(key)}')">View</button>`;
      }
      if (write && cfg.rowActions) actions += cfg.rowActions(r) || '';
      return actions;
    }),
    emptyText: cfg.emptyText || 'No records yet'
  });
}

route('#/customers', el => listPage(el, {
  perm: 'customers', title: 'Customers', sub: 'Customer master data',
  endpoint: '/customers', createLabel: 'New customer',
  searchKeys: ['name', 'companyName', 'email', 'gstin', 'phone', 'customerCode'],
  statusField: 'status', dateField: 'createdAt',
  columns: [
    { key: 'customerCode', label: 'Code' },
    { key: 'name', label: 'Name', render: r => escapeHtml(r.name || r.companyName || '—') },
    { key: 'email', label: 'Email' },
    { key: 'phone', label: 'Phone', render: r => escapeHtml(r.phone || r.phoneNumber || '—') },
    { key: 'gstin', label: 'GSTIN' },
    { key: 'status', label: 'Status', render: r => statusBadge(r.status || (r.active === false ? 'INACTIVE' : 'ACTIVE')) },
    { key: 'createdAt', label: 'Created', render: r => fmtDate(r.createdAt) }
  ],
  onCreate: () => openCustomerModal(),
  rowActions: r => {
    if (!can('customers', 'w')) return '';
    const s = String(r.status || 'ACTIVE').toUpperCase();
    if (s === 'INACTIVE')
      return `<button class="btn btn-outline-primary btn-sm" onclick="window.setCustomerStatus(${r.id},'ACTIVE')">Activate</button>`;
    return `<button class="btn btn-outline-secondary btn-sm" onclick="window.setCustomerStatus(${r.id},'INACTIVE')">Deactivate</button>`;
  },
  emptyText: 'No customers'
}));

route('#/inquiries', el => listPage(el, {
  perm: 'inquiries', title: 'Inquiries', sub: 'Inbound commercial inquiries',
  endpoint: '/inquiries', createLabel: 'New inquiry',
  onCreate: () => openInquiryModal(),
  searchKeys: ['inquiryNumber', 'subject', 'customerName', 'status'],
  dateField: 'createdAt',
  columns: [
    { key: 'inquiryNumber', label: 'Inquiry #' },
    { key: 'subject', label: 'Subject', render: r => escapeHtml(r.subject || '—') },
    { key: 'customerName', label: 'Customer', render: r => escapeHtml(r.customerName || r.customer?.name || r.customer?.companyName || '—') },
    { key: 'status', label: 'Status', render: r => statusBadge(r.status) },
    { key: 'createdAt', label: 'Created Date', render: r => fmtDate(r.createdAt || r.inquiryDate) }
  ]
}));

route('#/quotes', el => listPage(el, {
  perm: 'quotes', title: 'Quotes', sub: 'Commercial quotes',
  endpoint: '/quotes', createLabel: 'New quote',
  onCreate: () => openQuoteModal(),
  searchKeys: ['quoteNumber', 'status'],
  dateField: 'createdAt',
  columns: [
    { key: 'quoteNumber', label: 'Quote #' },
    { key: 'totalAmount', label: 'Amount', render: r => fmtMoney(r.totalAmount || r.amount) },
    { key: 'status', label: 'Status', render: r => statusBadge(r.status) },
    { key: 'validUntil', label: 'Valid until', render: r => fmtDate(r.validUntil) },
    { key: 'createdAt', label: 'Created', render: r => fmtDate(r.createdAt) }
  ],
  rowActions: r => {
    const s = String(r.status || '');
    let h = '';
    if (s === 'DRAFT') h += `<button class="btn btn-outline-primary btn-sm" onclick="window.actQuote(${r.id},'send')">Send</button>`;
    if (s === 'SENT') {
      h += `<button class="btn btn-outline-success btn-sm" onclick="window.actQuote(${r.id},'accept')">Accept</button>`;
      h += `<button class="btn btn-outline-danger btn-sm" onclick="window.actQuote(${r.id},'reject')">Reject</button>`;
    }
    if (s === 'ACCEPTED') h += `<button class="btn btn-primary btn-sm" onclick="window.actQuote(${r.id},'convert')">Convert to order</button>`;
    return h;
  }
}));

window.actQuote = async (id, action) => {
  try {
    if (action === 'convert') {
      await apiPost(`/quotes/${id}/convert-to-order`, {});
      toast('Order created from quote');
    } else {
      await apiPost(`/quotes/${id}/${action}`, {});
      toast(action === 'send' ? 'Quotation emailed to the selected customer' : 'Quote updated');
    }
    navigate();
  } catch (e) { toast(e.message, 'error'); }
};

route('#/orders', el => listPage(el, {
  perm: 'orders', title: 'Orders', sub: can('orders', 'w') ? 'Order lifecycle' : 'Orders (view only — no changes)',
  endpoint: '/orders', createLabel: can('orders', 'w') ? 'New order' : null,
  onCreate: can('orders', 'w') ? () => openOrderModal() : null,
  searchKeys: ['orderNumber', 'origin', 'destination', 'status'],
  dateField: 'orderDate',
  columns: [
    { key: 'orderNumber', label: 'Order #' },
    { key: 'origin', label: 'Origin' },
    { key: 'destination', label: 'Destination' },
    { key: 'status', label: 'Status', render: r => statusBadge(r.status) },
    { key: 'orderDate', label: 'Date', render: r => fmtDate(r.orderDate || r.createdAt) }
  ],
  rowActions: r => {
    if (!can('orders', 'w')) return '';
    const s = String(r.status || '');
    let h = '';
    if (s === 'DRAFT') h += `<button class="btn btn-outline-primary btn-sm" onclick="window.actOrder(${r.id},'confirm')">Confirm</button>`;
    if (s === 'CONFIRMED') h += `<button class="btn btn-outline-primary btn-sm" onclick="window.actOrder(${r.id},'status','PROCESSING')">Process</button>`;
    if (s === 'PROCESSING') h += `<button class="btn btn-outline-primary btn-sm" onclick="window.actOrder(${r.id},'status','READY_FOR_SHIPMENT')">Ready</button>`;
    if (!['DELIVERED', 'CANCELLED', 'SHIPPED'].includes(s)) {
      h += `<button class="btn btn-outline-danger btn-sm" onclick="window.actOrder(${r.id},'cancel')">Cancel</button>`;
    }
    return h;
  }
}));

window.actOrder = async (id, action, status) => {
  try {
    if (action === 'confirm') await apiPost(`/orders/${id}/confirm`, {});
    else if (action === 'cancel') await apiPost(`/orders/${id}/cancel`, {});
    else if (action === 'status') {
      const res = await api(`/orders/${id}/status?status=${encodeURIComponent(status)}`, { method: 'PATCH' });
      if (!res.ok) {
        const data = await res.json().catch(() => ({}));
        throw new Error(data.message || 'Status update failed');
      }
    }
    toast('Order updated');
    navigate();
  } catch (e) { toast(e.message, 'error'); }
};

route('#/bookings', el => listPage(el, {
  perm: 'bookings', title: 'Bookings', sub: 'Transport bookings',
  endpoint: '/bookings', createLabel: 'New booking',
  onCreate: () => openBookingModal(),
  searchKeys: ['bookingNumber', 'pickupLocation', 'destination', 'status'],
  dateField: 'createdAt',
  columns: [
    { key: 'bookingNumber', label: 'Booking #' },
    { key: 'orderNumber', label: 'Order #', render: r => escapeHtml(r.orderNumber || r.order?.orderNumber || '—') },
    { key: 'pickup', label: 'Pickup', render: r => escapeHtml(r.pickup || r.pickupLocation || '—') },
    { key: 'destination', label: 'Destination' },
    { key: 'status', label: 'Status', render: r => statusBadge(r.status) },
    { key: 'createdAt', label: 'Created', render: r => fmtDate(r.createdAt) }
  ]
}));

route('#/shipments', el => listPage(el, {
  perm: 'shipments', title: 'Shipments', sub: 'Physical movement of goods',
  endpoint: '/shipments', createLabel: 'New shipment',
  onCreate: () => openShipmentModal(),
  searchKeys: ['shipmentNumber', 'origin', 'destination', 'status'],
  dateField: 'createdAt',
  columns: [
    { key: 'shipmentNumber', label: 'Shipment #' },
    { key: 'orderNumber', label: 'Order #', render: r => escapeHtml(r.orderNumber || r.order?.orderNumber || '—') },
    { key: 'origin', label: 'Origin' },
    { key: 'destination', label: 'Destination' },
    { key: 'status', label: 'Status', render: r => statusBadge(r.status) },
    { key: 'expectedDelivery', label: 'ETA', render: r => fmtDate(r.expectedDelivery) },
    { key: 'createdAt', label: 'Created', render: r => fmtDate(r.createdAt) }
  ],
  rowActions: r => {
    if (!can('shipments', 'w')) return '';
    const s = String(r.status || '');
    let h = '';
    if (s === 'CREATED' || s === 'ASSIGNED') h += `<button class="btn btn-outline-secondary btn-sm" onclick="window.openAssignModal(${r.id})">Assign</button>`;
    if (s === 'CREATED' || s === 'ASSIGNED') h += `<button class="btn btn-outline-primary btn-sm" onclick="window.actShip(${r.id},'dispatch')">Dispatch</button>`;
    if (['CREATED', 'ASSIGNED', 'IN_TRANSIT', 'DELAYED'].includes(s)) {
      h += `<button class="btn btn-outline-success btn-sm" onclick="window.actShip(${r.id},'deliver')">Deliver</button>`;
    }
    if (!['DELIVERED', 'CANCELLED'].includes(s)) {
      h += `<button class="btn btn-outline-danger btn-sm" onclick="window.actShip(${r.id},'cancel')">Cancel</button>`;
    }
    return h;
  }
}));

window.actShip = async (id, action) => {
  try {
    if (action === 'dispatch') await apiPost(`/shipments/${id}/dispatch`, {});
    else if (action === 'cancel') await apiPost(`/shipments/${id}/cancel`, {});
    else if (action === 'deliver') {
      await apiPost(`/shipments/${id}/deliver`, {
        receivedBy: 'Consignee',
        remarks: 'Delivered via operations console',
        deliveryOutcome: 'DELIVERED'
      });
    }
    toast('Shipment updated');
    navigate();
  } catch (e) { toast(e.message, 'error'); }
};

route('#/tracking', async (el) => {
  if (!can('tracking', 'r')) {
    el.innerHTML = `<div class="alert alert-warning">No access</div>`;
    return;
  }
  let rows = [];
  try { rows = await loadList('/shipments'); } catch (e) {
    el.innerHTML = `<div class="alert alert-danger">${escapeHtml(e.message)}</div>`;
    return;
  }
  el.innerHTML = pageHead('Tracking', 'Select a shipment to view or simulate path') +
    `<div id="trkHost"></div><div id="trkDetail" class="mt-3"></div>`;
  mountTable('trkHost', {
    rows,
    searchKeys: ['shipmentNumber', 'status'],
    dateField: 'createdAt',
    columns: [
      { key: 'shipmentNumber', label: 'Shipment #' },
      { key: 'status', label: 'Status', render: r => statusBadge(r.status) },
      { key: 'origin', label: 'Origin' },
      { key: 'destination', label: 'Destination' }
    ],
    rowActions: r => {
      let h = `<button class="btn btn-outline-secondary btn-sm" onclick="window.viewTrack(${r.id})">View points</button>`;
      if (can('tracking', 'w')) h += `<button class="btn btn-outline-primary btn-sm" onclick="window.simTrack(${r.id})">Simulate</button>`;
      return h;
    }
  });
});

window.viewTrack = async (id) => {
  const box = document.getElementById('trkDetail');
  if (!box) return;
  try {
    const points = await apiGet(`/tracking/shipments/${id}`);
    const arr = Array.isArray(points) ? points : unwrapList(points);
    box.innerHTML = `<div class="work-panel p-3"><div class="d-flex justify-content-between align-items-center mb-3"><div class="fw-semibold">Tracking history · shipment ${id}</div><span class="badge text-bg-light">${arr.length} points</span></div>
      ${arr.length ? `<div class="table-responsive"><table class="table table-sm table-hover align-middle"><thead><tr><th>#</th><th>Trip</th><th>Location (lat, lng)</th><th>Speed</th><th>Status</th><th>Timestamp</th></tr></thead><tbody>${arr.map((p,i) => `<tr><td>${escapeHtml(p.id ?? i+1)}</td><td>${escapeHtml(p.tripId ?? '—')}</td><td>${escapeHtml(p.latitude ?? '—')}, ${escapeHtml(p.longitude ?? '—')}</td><td>${escapeHtml(p.speed ?? '—')} ${p.speed != null ? 'km/h' : ''}</td><td>${statusBadge(p.status || 'UNKNOWN')}</td><td>${escapeHtml(fmtDate(p.timestamp || p.recordedAt))}</td></tr>`).join('')}</tbody></table></div>` : '<span class="text-muted">No tracking points yet.</span>'}</div>`;
  } catch (e) { toast(e.message, 'error'); }
};
window.simTrack = async (id) => {
  try {
    await apiPost(`/tracking/shipments/${id}/simulate`, {});
    toast('Simulation recorded');
    window.viewTrack(id);
  } catch (e) { toast(e.message, 'error'); }
};

route('#/warehouses', el => listPage(el, {
  perm: 'warehouses', title: 'Warehouses', sub: can('warehouses', 'w') ? 'Capacity & locations' : 'Warehouses (view only)',
  endpoint: '/warehouses', createLabel: can('warehouses', 'w') ? 'New warehouse' : null,
  onCreate: () => openWarehouseModal(),
  searchKeys: ['warehouseCode', 'name', 'city', 'status'],
  columns: [
    { key: 'warehouseCode', label: 'Code' },
    { key: 'name', label: 'Name' },
    { key: 'city', label: 'City' },
    { key: 'capacity', label: 'Capacity' },
    { key: 'availableCapacity', label: 'Stock Available' },
    { key: 'status', label: 'Status', render: r => statusBadge(r.status) }
  ],
  rowActions: r => {
    if (!can('warehouses', 'w')) return '';
    const s = String(r.status || '').toUpperCase();
    if (s === 'ACTIVE') return `<button class="btn btn-outline-secondary btn-sm" onclick="window.toggleEntityStatus('/warehouses',${r.id},'status','INACTIVE')">Deactivate</button>`;
    return `<button class="btn btn-outline-primary btn-sm" onclick="window.toggleEntityStatus('/warehouses',${r.id},'status','ACTIVE')">Activate</button>`;
  }
}));

route('#/commodities', el => listPage(el, {
  perm: 'commodities', title: 'Commodities', sub: 'Stock lines',
  endpoint: '/commodities', createLabel: can('commodities', 'w') ? 'New commodity' : null,
  onCreate: can('commodities', 'w') ? () => openCommodityModal() : null,
  searchKeys: ['name', 'sku', 'status'],
  columns: [
    { key: 'name', label: 'Name' },
    { key: 'sku', label: 'SKU', render: r => escapeHtml(r.sku || r.code || '—') },
    { key: 'quantity', label: 'Qty', render: r => r.quantity ?? r.availableQuantity ?? '—' },
    { key: 'status', label: 'Status', render: r => statusBadge(r.status || (r.active === false ? 'INACTIVE' : 'ACTIVE')) }
  ]
}));

route('#/receipts', el => listPage(el, {
  perm: 'receipts', title: 'Goods receipts', sub: 'Inbound stock',
  endpoint: '/receipts', createLabel: can('receipts', 'w') ? 'New receipt' : null,
  onCreate: can('receipts', 'w') ? () => openReceiptModal() : null,
  searchKeys: ['receiptNumber', 'status'],
  dateField: 'receivedDate',
  columns: [
    { key: 'receiptNumber', label: 'Receipt #' },
    { key: 'quantity', label: 'Qty' },
    { key: 'receivedDate', label: 'Received', render: r => fmtDate(r.receivedDate || r.createdAt) },
    { key: 'status', label: 'Status', render: r => statusBadge(r.status) }
  ]
}));

route('#/stock-transfers', el => listPage(el, {
  perm: 'stockTransfers', title: 'Stock transfers', sub: 'Inter-warehouse movement',
  endpoint: '/stock-transfers', createLabel: can('stockTransfers', 'w') ? 'New transfer' : null,
  onCreate: can('stockTransfers', 'w') ? () => openTransferModal() : null,
  searchKeys: ['transferNumber', 'status'],
  dateField: 'createdAt',
  columns: [
    { key: 'transferNumber', label: 'Transfer #' },
    { key: 'quantity', label: 'Qty' },
    { key: 'status', label: 'Status', render: r => statusBadge(r.status) },
    { key: 'createdAt', label: 'Date', render: r => fmtDate(r.createdAt) }
  ]
}));

route('#/drivers', el => listPage(el, {
  perm: 'drivers', title: 'Drivers', sub: 'Fleet drivers',
  endpoint: '/drivers', createLabel: 'New driver',
  onCreate: () => openDriverModal(),
  searchKeys: ['driverCode', 'name', 'phone', 'licenseNumber', 'status'],
  columns: [
    { key: 'driverCode', label: 'Code' },
    { key: 'name', label: 'Name' },
    { key: 'phone', label: 'Phone' },
    { key: 'licenseNumber', label: 'License' },
    { key: 'status', label: 'Status', render: r => statusBadge(r.status) }
  ],
  rowActions: r => {
    if (!can('drivers', 'w')) return '';
    const s = String(r.status || '').toUpperCase();
    if (s === 'ACTIVE') return `<button class="btn btn-outline-secondary btn-sm" onclick="window.toggleEntityStatus('/drivers',${r.id},'status','INACTIVE')">Deactivate</button>`;
    return `<button class="btn btn-outline-primary btn-sm" onclick="window.toggleEntityStatus('/drivers',${r.id},'status','ACTIVE')">Activate</button>`;
  }
}));

route('#/vehicles', el => listPage(el, {
  perm: 'vehicles', title: 'Vehicles', sub: 'Fleet vehicles',
  endpoint: '/vehicles', createLabel: 'New vehicle',
  onCreate: () => openVehicleModal(),
  searchKeys: ['vehicleNumber', 'type', 'status'],
  columns: [
    { key: 'vehicleNumber', label: 'Number', render: r => escapeHtml(r.vehicleNumber || r.registrationNumber || '—') },
    { key: 'type', label: 'Type', render: r => escapeHtml(r.type || r.vehicleType || '—') },
    { key: 'capacity', label: 'Capacity' },
    { key: 'status', label: 'Status', render: r => statusBadge(r.status) }
  ],
  rowActions: r => {
    if (!can('vehicles', 'w')) return '';
    const s = String(r.status || '').toUpperCase();
    if (s === 'ACTIVE') return `<button class="btn btn-outline-secondary btn-sm" onclick="window.toggleEntityStatus('/vehicles',${r.id},'status','INACTIVE')">Deactivate</button>`;
    return `<button class="btn btn-outline-primary btn-sm" onclick="window.toggleEntityStatus('/vehicles',${r.id},'status','ACTIVE')">Activate</button>`;
  }
}));

route('#/trips', el => listPage(el, {
  perm: 'trips', title: 'Trips', sub: 'Trip register',
  endpoint: '/trips',
  searchKeys: ['tripNumber', 'status'],
  dateField: 'createdAt',
  columns: [
    { key: 'tripNumber', label: 'Trip #' },
    { key: 'origin', label: 'Pickup location', render: r => escapeHtml(r.origin || '—') },
    { key: 'destination', label: 'Delivery location', render: r => escapeHtml(r.destination || '—') },
    { key: 'status', label: 'Status', render: r => statusBadge(r.status) }
  ],
}));

route('#/invoices', el => listPage(el, {
  perm: 'invoices', title: 'Invoices', sub: 'Billing',
  endpoint: '/invoices', createLabel: 'New invoice',
  onCreate: () => openInvoiceModal(),
  searchKeys: ['invoiceNumber', 'status'],
  dateField: 'invoiceDate',
  columns: [
    { key: 'invoiceNumber', label: 'Invoice #' },
    { key: 'total', label: 'Total', render: r => fmtMoney(r.total) },
    { key: 'status', label: 'Status', render: r => statusBadge(r.status) },
    { key: 'dueDate', label: 'Due', render: r => fmtDate(r.dueDate) },
    { key: 'invoiceDate', label: 'Issued', render: r => fmtDate(r.invoiceDate) }
  ],
  rowActions: r => {
    let h = `<button class="btn btn-outline-secondary btn-sm" onclick="window.viewInvoice(${r.id})">View PDF</button>`;
    if (can('invoices', 'w')) {
      const s = String(r.status || '');
      h += `<button class="btn btn-outline-primary btn-sm" onclick="window.actInv(${r.id},'send')">Send email</button>`;
      if (s === 'ISSUED' || s === 'OVERDUE') h += `<button class="btn btn-outline-success btn-sm" onclick="window.actInv(${r.id},'pay')">Mark paid</button>`;
      if (s !== 'PAID' && s !== 'CANCELLED') h += `<button class="btn btn-outline-danger btn-sm" onclick="window.actInv(${r.id},'cancel')">Cancel</button>`;
    }
    return h;
  }
}));

window.actInv = async (id, action) => {
  try {
    await apiPost(`/invoices/${id}/${action}`, {});
    toast(action === 'send' ? 'Invoice emailed to the selected customer' : 'Invoice updated');
    navigate();
  } catch (e) { toast(e.message, 'error'); }
};

window.viewInvoice = async (id) => {
  try {
    const inv = await apiGet(`/invoices/${id}`);
    const [customers, orders, shipments] = await Promise.all([loadList('/customers'), loadList('/orders'), loadList('/shipments')]);
    const customer = customers.find(c => Number(c.id) === Number(inv.customerId)) || {};
    const order = orders.find(o => Number(o.id) === Number(inv.orderId)) || {};
    const shipment = shipments.find(x => Number(x.id) === Number(inv.shipmentId)) || {};
    const money = n => '₹' + Number(n || 0).toLocaleString('en-IN', {minimumFractionDigits:2, maximumFractionDigits:2});
    const w = window.open('', '_blank');
    if (!w) { toast('Allow pop-ups to view the invoice PDF layout.', 'error'); return; }
    w.document.write(`<!doctype html><html><head><title>Invoice ${escapeHtml(inv.invoiceNumber || '')}</title><style>body{font-family:Arial,sans-serif;color:#172033;margin:36px}.brand{background:#12366b;color:white;padding:24px;border-radius:8px}.brand h1{margin:0}.meta{display:flex;justify-content:space-between;margin:24px 0}.panel{border:1px solid #dbe2ea;border-radius:8px;padding:16px;min-width:40%}table{width:100%;border-collapse:collapse;margin-top:24px}th,td{border:1px solid #dbe2ea;padding:12px;text-align:left}th{background:#f1f5f9}.total{text-align:right;font-size:20px;font-weight:bold;margin-top:20px}.muted{color:#64748b}@media print{button{display:none}body{margin:15mm}}</style></head><body><div class="brand"><div style="font-size:12px;letter-spacing:2px">ASTERA LNX</div><h1>INVOICE</h1><div>Logistics Management</div></div><div class="meta"><div class="panel"><b>Bill To</b><p>${escapeHtml(customer.companyName || customer.name || 'Customer')}</p><p>${escapeHtml(customer.address || '')}</p><p>${escapeHtml(customer.email || '')}</p><p>${escapeHtml(customer.phone || '')}</p></div><div class="panel"><p><b>Invoice #:</b> ${escapeHtml(inv.invoiceNumber || '—')}</p><p><b>Invoice date:</b> ${escapeHtml(inv.invoiceDate || '—')}</p><p><b>Due date:</b> ${escapeHtml(inv.dueDate || '—')}</p><p><b>Status:</b> ${escapeHtml(inv.status || '—')}</p><p><b>Order #:</b> ${escapeHtml(order.orderNumber || '—')}</p><p><b>Shipment #:</b> ${escapeHtml(shipment.shipmentNumber || '—')}</p></div></div><table><thead><tr><th>Description</th><th>Amount</th></tr></thead><tbody><tr><td>Logistics / shipment services</td><td>${money(inv.subtotal)}</td></tr><tr><td>Tax</td><td>${money(inv.tax)}</td></tr><tr><td>Discount</td><td>−${money(inv.discount)}</td></tr></tbody></table><div class="total">Grand Total: ${money(inv.total)}</div><p class="muted">Thank you for your business. Please contact AsteraLNX Logistics for invoice queries.</p><button onclick="window.print()">Print / Save as PDF</button><script>window.onload=()=>setTimeout(()=>window.print(),300)</script></body></html>`);
    w.document.close();
  } catch (e) { toast(e.message, 'error'); }
};

route('#/my-deliveries', el => listPage(el, {
  perm: 'myDeliveries', title: 'My deliveries', sub: 'Shipments assigned to you',
  endpoint: '/shipments/mine',
  searchKeys: ['shipmentNumber', 'status', 'origin', 'destination'],
  columns: [
    { key: 'shipmentNumber', label: 'Shipment #' },
    { key: 'origin', label: 'Origin' },
    { key: 'destination', label: 'Destination' },
    { key: 'status', label: 'Status', render: r => statusBadge(r.status) }
  ]
}));

route('#/my-trips', el => listPage(el, {
  perm: 'myTrips', title: 'My trips', sub: 'Your trip assignments',
  endpoint: '/trips/mine',
  searchKeys: ['tripNumber', 'status'],
  columns: [
    { key: 'tripNumber', label: 'Trip #' },
    { key: 'origin', label: 'Pickup location', render: r => escapeHtml(r.origin || '—') },
    { key: 'destination', label: 'Delivery location', render: r => escapeHtml(r.destination || '—') },
    { key: 'status', label: 'Status', render: r => statusBadge(r.status) }
  ],
  rowActions: r => ['COMPLETED','CANCELLED'].includes(String(r.status || '').toUpperCase()) ? '' : `<button class="btn btn-success btn-sm" onclick="window.driverDeliver(${r.shipmentId})">Delivered</button>`
}));

window.driverDeliver = async (shipmentId) => {
  if (!confirm('Confirm this delivery is complete?')) return;
  try { await apiPost(`/shipments/${shipmentId}/deliver`, { receivedBy: 'Recipient', remarks: 'Delivery completed by assigned driver', outcome: 'DELIVERED' }); toast('Delivery completed. Shipment status updated.'); navigate(); }
  catch (e) { toast(e.message, 'error'); }
};

/* —— Reports: list view + filters + export —— */
route('#/reports', async (el) => {
  if (!can('reports', 'r')) {
    el.innerHTML = `<div class="alert alert-warning">Reports are available to Admin only.</div>`;
    return;
  }
  const types = [
    { key: 'orders', label: 'Orders', endpoint: '/orders', cols: [
      { key: 'orderNumber', label: 'Order #' }, { key: 'origin', label: 'Origin' },
      { key: 'destination', label: 'Destination' }, { key: 'status', label: 'Status', render: r => statusBadge(r.status) },
      { key: 'orderDate', label: 'Date', render: r => fmtDate(r.orderDate || r.createdAt) }
    ], searchKeys: ['orderNumber', 'origin', 'destination'], dateField: 'orderDate' },
    { key: 'shipments', label: 'Shipments', endpoint: '/shipments', cols: [
      { key: 'shipmentNumber', label: 'Shipment #' }, { key: 'origin', label: 'Origin' },
      { key: 'destination', label: 'Destination' }, { key: 'status', label: 'Status', render: r => statusBadge(r.status) },
      { key: 'createdAt', label: 'Date', render: r => fmtDate(r.createdAt) }
    ], searchKeys: ['shipmentNumber', 'status'], dateField: 'createdAt' },
    { key: 'invoices', label: 'Invoices / revenue', endpoint: '/invoices', cols: [
      { key: 'invoiceNumber', label: 'Invoice #' }, { key: 'total', label: 'Total', render: r => fmtMoney(r.total) },
      { key: 'status', label: 'Status', render: r => statusBadge(r.status) },
      { key: 'dueDate', label: 'Due', render: r => fmtDate(r.dueDate) }
    ], searchKeys: ['invoiceNumber', 'status'], dateField: 'invoiceDate' },
    { key: 'customers', label: 'Customers', endpoint: '/customers', cols: [
      { key: 'customerCode', label: 'Code' }, { key: 'name', label: 'Name', render: r => escapeHtml(r.name || r.companyName || '—') },
      { key: 'email', label: 'Email' }, { key: 'gstin', label: 'GSTIN' }
    ], searchKeys: ['name', 'email', 'gstin'] },
    { key: 'warehouses', label: 'Warehouses', endpoint: '/warehouses', cols: [
      { key: 'warehouseCode', label: 'Code' }, { key: 'name', label: 'Name' },
      { key: 'city', label: 'City' }, { key: 'availableCapacity', label: 'Stock Available' }
    ], searchKeys: ['name', 'city', 'warehouseCode'] },
    { key: 'drivers', label: 'Drivers', endpoint: '/drivers', cols: [
      { key: 'driverCode', label: 'Code' }, { key: 'name', label: 'Name' },
      { key: 'status', label: 'Status', render: r => statusBadge(r.status) }
    ], searchKeys: ['name', 'driverCode'] }
  ];

  let summary = {};
  try { summary = await apiGet('/reports/summary') || {}; } catch (_) {}

  el.innerHTML = pageHead('Reports', 'Browse filtered data, then export CSV or PDF') +
    `<div class="kpi-row mb-3">
      <div class="kpi"><div class="lbl">Orders</div><div class="val">${summary.orders ?? '—'}</div></div>
      <div class="kpi"><div class="lbl">Shipments</div><div class="val">${summary.shipments ?? '—'}</div></div>
      <div class="kpi"><div class="lbl">Invoices</div><div class="val">${summary.invoices ?? '—'}</div></div>
      <div class="kpi"><div class="lbl">Overdue invoices</div><div class="val">${summary.invoicesOverdue ?? '—'}</div></div>
    </div>
    <div class="report-layout">
      <div class="report-nav" id="reportNav">
        ${types.map((t, i) => `<button type="button" data-i="${i}" class="${i === 0 ? 'active' : ''}">${t.label}</button>`).join('')}
      </div>
      <div>
        <div class="d-flex flex-wrap gap-2 mb-2 justify-content-end">
          <button type="button" class="btn btn-outline-success btn-sm" id="btnCsv"><i class="bi bi-filetype-csv me-1"></i>CSV</button>
          <button type="button" class="btn btn-outline-danger btn-sm" id="btnPdf"><i class="bi bi-filetype-pdf me-1"></i>PDF</button>
        </div>
        <div id="reportTable"></div>
      </div>
    </div>`;

  let active = 0;
  async function showReport(i) {
    active = i;
    document.querySelectorAll('#reportNav button').forEach((b, idx) => b.classList.toggle('active', idx === i));
    const t = types[i];
    const host = document.getElementById('reportTable');
    host.innerHTML = '<div class="text-muted p-3">Loading…</div>';
    try {
      const rows = await loadList(t.endpoint);
      host.innerHTML = '<div id="rptHost"></div>';
      mountTable('rptHost', {
        rows,
        columns: t.cols,
        searchKeys: t.searchKeys,
        dateField: t.dateField || null,
        statusField: 'status',
        emptyText: 'No rows for this report'
      });
    } catch (e) {
      host.innerHTML = `<div class="alert alert-danger">${escapeHtml(e.message)}</div>`;
    }
  }
  document.querySelectorAll('#reportNav button').forEach(btn => {
    btn.addEventListener('click', () => showReport(Number(btn.getAttribute('data-i'))));
  });
  document.getElementById('btnCsv')?.addEventListener('click', () => window.downloadReport(types[active].key, 'csv'));
  document.getElementById('btnPdf')?.addEventListener('click', () => window.downloadReport(types[active].key, 'pdf'));
  await showReport(0);
});

window.downloadReport = async function (type, format) {
  try {
    const token = getToken();
    const res = await fetch(`/api/reports/${type}/${format}`, {
      headers: { Authorization: 'Bearer ' + token }
    });
    if (!res.ok) {
      const err = await res.json().catch(() => ({}));
      throw new Error(err.message || 'Export failed');
    }
    const blob = await res.blob();
    const url = URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = `${type}-report.${format}`;
    document.body.appendChild(a);
    a.click();
    a.remove();
    URL.revokeObjectURL(url);
    toast(format.toUpperCase() + ' downloaded');
  } catch (e) {
    toast(e.message, 'error');
  }
};

/* —— Users (ADMIN) —— */
route('#/users', async (el) => {
  if (!can('users', 'w')) {
    el.innerHTML = `<div class="alert alert-danger">Only Admin can manage users.</div>`;
    return;
  }
  let rows = [];
  try { rows = await loadList('/users'); } catch (e) {
    el.innerHTML = pageHead('Users', 'Internal accounts') + `<div class="alert alert-danger">${escapeHtml(e.message)}</div>`;
    return;
  }
  el.innerHTML = pageHead('Users', 'Create and manage internal users',
    `<button type="button" class="btn btn-primary btn-sm" id="btnOpenCreateUser"><i class="bi bi-person-plus me-1"></i>Create user</button>`) +
    `<div id="userHost"></div>`;
  document.getElementById('btnOpenCreateUser')?.addEventListener('click', () => openUserModal());
  mountTable('userHost', {
    rows,
    searchKeys: ['fullName', 'email', 'role'],
    statusField: 'active',
    columns: [
      { key: 'fullName', label: 'Name' },
      { key: 'email', label: 'Email' },
      { key: 'role', label: 'Role', render: r => escapeHtml(String(r.role || '').replace(/_/g, ' ')) },
      { key: 'active', label: 'Status', render: r => statusBadge(r.active === false ? 'INACTIVE' : 'ACTIVE') }
    ],
    rowActions: r => {
      if (r.active === false)
        return `<button class="btn btn-outline-primary btn-sm" onclick="window.toggleEntityStatus('/users',${r.id},'active','true')">Activate</button>`;
      return `<button class="btn btn-outline-secondary btn-sm" onclick="window.toggleEntityStatus('/users',${r.id},'active','false')">Deactivate</button>`;
    }
  });
});


/* —— Modal helpers & working creates —— */
function ensureModalRoot() {
  let root = document.getElementById('appModalRoot');
  if (!root) {
    root = document.createElement('div');
    root.id = 'appModalRoot';
    document.body.appendChild(root);
  }
  return root;
}

function showFormModal(title, fieldsHtml, onSubmit) {
  const root = ensureModalRoot();
  root.innerHTML = `
  <div class="modal fade show d-block" tabindex="-1" style="background:rgba(0,0,0,.4)" id="dynModal">
    <div class="modal-dialog modal-dialog-centered modal-lg">
      <div class="modal-content">
        <div class="modal-header">
          <h5 class="modal-title">${title}</h5>
          <button type="button" class="btn-close" id="dynModalClose"></button>
        </div>
        <div class="modal-body"><form id="dynForm" novalidate>${fieldsHtml}
          <div id="dynFormErr" class="alert alert-danger d-none py-2 small mt-2"></div>
        </form></div>
        <div class="modal-footer">
          <button type="button" class="btn btn-outline-secondary btn-sm" id="dynModalCancel">Cancel</button>
          <button type="button" class="btn btn-primary btn-sm" id="dynModalSave">Save</button>
        </div>
      </div>
    </div>
  </div>`;
  const close = () => { root.innerHTML = ''; };
  document.getElementById('dynModalClose')?.addEventListener('click', close);
  document.getElementById('dynModalCancel')?.addEventListener('click', close);
  document.getElementById('dynModalSave')?.addEventListener('click', async () => {
    const err = document.getElementById('dynFormErr');
    const form = document.getElementById('dynForm');
    err?.classList.add('d-none');
    if (form && !form.checkValidity()) {
      form.reportValidity();
      return;
    }
    const btn = document.getElementById('dynModalSave');
    if (btn) btn.disabled = true;
    try {
      await onSubmit(form);
      close();
      toast('Saved successfully');
      navigate();
    } catch (e) {
      if (err) { err.textContent = e.message || 'Save failed'; err.classList.remove('d-none'); }
      else toast(e.message || 'Save failed', 'error');
    } finally {
      if (btn) btn.disabled = false;
    }
  });
}

function val(form, name) {
  const el = form.querySelector(`[name="${name}"]`);
  return el ? el.value.trim() : '';
}
async function optionList(path, idKey, labelFn) {
  try {
    const rows = await loadList(path);
    return rows.map(r => `<option value="${r[idKey]}">${escapeHtml(labelFn(r))}</option>`).join('');
  } catch (e) {
    return `<option value="">(${escapeHtml(e.message)})</option>`;
  }
}

function openCustomerModal() {
  showFormModal('New customer', `
    <div class="row g-2">
      <div class="col-md-6"><label class="form-label required">Company name</label><input class="form-control form-control-sm" name="companyName" required maxlength="50"></div>
      <div class="col-md-6"><label class="form-label required">Contact person</label><input class="form-control form-control-sm" name="contactPerson" required maxlength="50"></div>
      <div class="col-md-6"><label class="form-label required">Phone</label><input class="form-control form-control-sm" name="phone" required placeholder="+91 9876543210"></div>
      <div class="col-md-6"><label class="form-label required">Email</label><input type="email" class="form-control form-control-sm" name="email" required></div>
      <div class="col-md-6"><label class="form-label required">GSTIN (15 chars)</label><input class="form-control form-control-sm" name="gstNumber" required maxlength="15" placeholder="29ABCDE1234F1Z5"></div>
      <div class="col-md-6"><label class="form-label required">Postal code</label><input class="form-control form-control-sm" name="postalCode" required></div>
      <div class="col-12"><label class="form-label required">Address</label><input class="form-control form-control-sm" name="address" required maxlength="255"></div>
      <div class="col-md-4"><label class="form-label required">City</label><input class="form-control form-control-sm" name="city" required></div>
      <div class="col-md-4"><label class="form-label required">State</label><input class="form-control form-control-sm" name="state" required></div>
      <div class="col-md-4"><label class="form-label required">Country</label><input class="form-control form-control-sm" name="country" required value="India"></div>
    </div>`, async (form) => {
    const body = {
      companyName: val(form, 'companyName'), contactPerson: val(form, 'contactPerson'),
      phone: val(form, 'phone'), email: val(form, 'email'), gstNumber: val(form, 'gstNumber').toUpperCase(),
      address: val(form, 'address'), city: val(form, 'city'), state: val(form, 'state'),
      country: val(form, 'country'), postalCode: val(form, 'postalCode')
    };
    if (body.gstNumber.length !== 15) throw new Error('GSTIN must be exactly 15 characters');
    await apiPost('/customers', body);
  });
}

function openWarehouseModal() {
  showFormModal('New warehouse', `
    <div class="row g-2">
      <div class="col-md-6"><label class="form-label required">Name</label><input class="form-control form-control-sm" name="name" required maxlength="100"></div>
      <div class="col-md-6"><label class="form-label required">City</label><input class="form-control form-control-sm" name="city" required></div>
      <div class="col-12"><label class="form-label required">Address</label><input class="form-control form-control-sm" name="address" required></div>
      <div class="col-md-4"><label class="form-label required">State</label><input class="form-control form-control-sm" name="state" required></div>
      <div class="col-md-4"><label class="form-label required">Country</label><input class="form-control form-control-sm" name="country" required value="India"></div>
      <div class="col-md-4"><label class="form-label required">Postal code</label><input class="form-control form-control-sm" name="postalCode" required></div>
      <div class="col-md-4"><label class="form-label required">Capacity</label><input type="number" min="0.01" step="0.01" class="form-control form-control-sm" name="capacity" required value="1000"></div>
      <div class="col-md-4"><label class="form-label">Manager name</label><input class="form-control form-control-sm" name="managerName"></div>
    </div>`, async (form) => {
    const body = {
      name: val(form, 'name'),
      city: val(form, 'city'),
      address: val(form, 'address'),
      state: val(form, 'state'),
      country: val(form, 'country'),
      postalCode: val(form, 'postalCode'),
      capacity: Number(val(form, 'capacity')),
      managerName: val(form, 'managerName') || null
    };
    await apiPost('/warehouses', body);
  });
}

function openUserModal() {
  showFormModal('Create user', `
    <div class="row g-2">
      <div class="col-md-6"><label class="form-label required">Full name</label><input class="form-control form-control-sm" name="fullName" required minlength="2"></div>
      <div class="col-md-6"><label class="form-label required">Email</label><input type="email" class="form-control form-control-sm" name="email" required></div>
      <div class="col-md-6"><label class="form-label required">Password</label><input type="password" class="form-control form-control-sm" name="password" required minlength="8" placeholder="Upper, lower, number, special"></div>
      <div class="col-md-6"><label class="form-label">Phone</label><input class="form-control form-control-sm" name="phoneNumber"></div>
      <div class="col-md-6"><label class="form-label required">Role</label>
        <select class="form-select form-select-sm" name="role" required>
          <option value="OPERATIONS_MANAGER">Operations Manager</option>
          <option value="WAREHOUSE_MANAGER">Warehouse Manager</option>
          <option value="DRIVER">Driver</option>
          <option value="ADMIN">Admin</option>
        </select>
      </div>
    </div>`, async (form) => {
    await apiPost('/users', {
      fullName: val(form, 'fullName'), email: val(form, 'email'),
      password: val(form, 'password'), phoneNumber: val(form, 'phoneNumber') || null,
      role: val(form, 'role')
    });
  });
}

function openDriverModal() {
  showFormModal('New driver', `
    <div class="row g-2">
      <div class="col-md-6"><label class="form-label required">Name</label><input class="form-control form-control-sm" name="name" required></div>
      <div class="col-md-6"><label class="form-label required">Phone</label><input class="form-control form-control-sm" name="phone" required></div>
      <div class="col-md-6"><label class="form-label">Email</label><input type="email" class="form-control form-control-sm" name="email"></div>
      <div class="col-md-6"><label class="form-label required">License number</label><input class="form-control form-control-sm" name="licenseNumber" required></div>
      <div class="col-md-6"><label class="form-label required">License expiry</label><input type="date" class="form-control form-control-sm" name="licenseExpiry" required></div>
    </div>`, async (form) => {
    await apiPost('/drivers', {
      name: val(form, 'name'), phone: val(form, 'phone'), email: val(form, 'email') || null,
      licenseNumber: val(form, 'licenseNumber'), licenseExpiry: val(form, 'licenseExpiry')
    });
  });
}



function openOrderModal() {
  (async () => {
    const cust = await optionList('/customers', 'id', r => (r.companyName || r.name || ('#'+r.id)) + ' · ' + (r.email||''));
    showFormModal('New order', `
      <div class="row g-2">
        <div class="col-md-6"><label class="form-label required">Customer</label>
          <select class="form-select form-select-sm" name="customerId" required><option value="">Select…</option>${cust}</select></div>
        <div class="col-md-3"><label class="form-label required">Origin</label><input class="form-control form-control-sm" name="origin" required></div>
        <div class="col-md-3"><label class="form-label required">Destination</label><input class="form-control form-control-sm" name="destination" required></div>
        <div class="col-md-4"><label class="form-label required">Commodity name</label><input class="form-control form-control-sm" name="commodityName" required></div>
        <div class="col-md-2"><label class="form-label required">Qty</label><input type="number" min="0.01" step="0.01" class="form-control form-control-sm" name="quantity" required value="1"></div>
        <div class="col-md-2"><label class="form-label">Weight</label><input type="number" min="0" step="0.01" class="form-control form-control-sm" name="weight"></div>
        <div class="col-md-2"><label class="form-label">Volume</label><input type="number" min="0" step="0.01" class="form-control form-control-sm" name="volume"></div>
        <div class="col-md-2"><label class="form-label">Priority</label>
          <select class="form-select form-select-sm" name="priority"><option value="NORMAL">NORMAL</option><option value="HIGH">HIGH</option><option value="LOW">LOW</option></select></div>
      </div>`, async (form) => {
      const body = {
        customerId: Number(val(form, 'customerId')),
        origin: val(form, 'origin'), destination: val(form, 'destination'),
        commodityName: val(form, 'commodityName'), quantity: Number(val(form, 'quantity')),
        weight: val(form, 'weight') ? Number(val(form, 'weight')) : null,
        volume: val(form, 'volume') ? Number(val(form, 'volume')) : null,
        priority: val(form, 'priority') || 'NORMAL'
      };
      await apiPost('/orders', body);
    });
  })().catch(e => toast(e.message, 'error'));
}

function openShipmentModal() {
  (async () => {
    const orders = await optionList('/orders', 'id', r => (r.orderNumber || ('#'+r.id)) + ' · ' + (r.status||''));
    const wh = await optionList('/warehouses', 'id', r => (r.warehouseCode || '') + ' ' + (r.name||('#'+r.id)));
    const drivers = await optionList('/drivers', 'id', r => (r.name || ('#'+r.id)));
    const vehicles = await optionList('/vehicles', 'id', r => (r.vehicleNumber || ('#'+r.id)));
    const commodities = await optionList('/commodities', 'id', r => (r.name || ('#'+r.id)));
    showFormModal('New shipment', `
      <div class="row g-2">
        <div class="col-md-6"><label class="form-label required">Order</label>
          <select class="form-select form-select-sm" name="orderId" required><option value="">Select…</option>${orders}</select></div>
        <div class="col-md-6"><label class="form-label required">Warehouse</label>
          <select class="form-select form-select-sm" name="warehouseId" required><option value="">Select warehouse…</option>${wh}</select></div>
        <div class="col-md-4"><label class="form-label required">Commodity</label>
          <select class="form-select form-select-sm" name="commodityId" required><option value="">Select commodity…</option>${commodities}</select></div>
        <div class="col-md-4"><label class="form-label required">Qty</label><input type="number" min="0.01" step="0.01" class="form-control form-control-sm" name="quantity" required></div>
        <div class="col-md-4"><label class="form-label">Expected delivery</label><input type="date" class="form-control form-control-sm" name="expectedDelivery" required></div>
        <div class="col-md-6"><label class="form-label required">Driver</label>
          <select class="form-select form-select-sm" name="driverId" required><option value="">Select driver…</option>${drivers}</select></div>
        <div class="col-md-6"><label class="form-label required">Vehicle</label>
          <select class="form-select form-select-sm" name="vehicleId" required><option value="">Select vehicle…</option>${vehicles}</select></div>
      </div>`, async (form) => {
      const body = {
        orderId: Number(val(form, 'orderId')),
        warehouseId: val(form, 'warehouseId') ? Number(val(form, 'warehouseId')) : null,
        commodityId: val(form, 'commodityId') ? Number(val(form, 'commodityId')) : null,
        quantity: val(form, 'quantity') ? Number(val(form, 'quantity')) : null,
        expectedDelivery: val(form, 'expectedDelivery') || null,
        driverId: val(form, 'driverId') ? Number(val(form, 'driverId')) : null,
        vehicleId: val(form, 'vehicleId') ? Number(val(form, 'vehicleId')) : null
      };
      await apiPost('/shipments', body);
    });
  })().catch(e => toast(e.message, 'error'));
}

function openCommodityModal() {
  (async () => {
    const wh = await optionList('/warehouses', 'id', r => (r.name || ('#'+r.id)));
    showFormModal('New commodity', `
      <div class="row g-2">
        <div class="col-md-6"><label class="form-label required">Name</label><input class="form-control form-control-sm" name="name" required></div>
        <div class="col-md-3"><label class="form-label required">Unit</label><select class="form-select form-select-sm" name="unit" required><option value="Pieces">Pieces</option><option value="Each">Each</option><option value="Kilograms" selected>Kilograms</option><option value="Grams">Grams</option><option value="Litres">Litres</option><option value="Metres">Metres</option><option value="Dozen">Dozen</option><option value="Boxes">Boxes</option><option value="Cartons">Cartons</option><option value="Pallets">Pallets</option></select></div>
        <div class="col-md-3"><label class="form-label required">Quantity</label><input type="number" min="0" step="0.01" class="form-control form-control-sm" name="quantity" required value="0"></div>
        <div class="col-md-6"><label class="form-label required">Warehouse</label>
          <select class="form-select form-select-sm" name="warehouseId" required><option value="">Select…</option>${wh}</select></div>
        <div class="col-md-3"><label class="form-label">Category</label><input class="form-control form-control-sm" name="category"></div>
        <div class="col-md-3"><label class="form-label">Storage location</label><input class="form-control form-control-sm" name="storageLocation"></div>
      </div>`, async (form) => {
      await apiPost('/commodities', {
        name: val(form, 'name'), unit: val(form, 'unit'), quantity: Number(val(form, 'quantity')),
        warehouseId: Number(val(form, 'warehouseId')), category: val(form, 'category') || null,
        storageLocation: val(form, 'storageLocation') || null
      });
    });
  })().catch(e => toast(e.message, 'error'));
}

function openVehicleModal() {
  showFormModal('New vehicle', `
    <div class="row g-2">
      <div class="col-md-4"><label class="form-label required">Vehicle number</label><input class="form-control form-control-sm" name="vehicleNumber" required></div>
      <div class="col-md-4"><label class="form-label required">Type</label><input class="form-control form-control-sm" name="vehicleType" required placeholder="Truck / Van"></div>
      <div class="col-md-4"><label class="form-label required">Capacity</label><input type="number" min="0.01" step="0.01" class="form-control form-control-sm" name="capacity" required value="10"></div>
      <div class="col-md-6"><label class="form-label">Registration</label><input class="form-control form-control-sm" name="registrationNumber"></div>
      <div class="col-md-6"><label class="form-label">Insurance expiry</label><input type="date" class="form-control form-control-sm" name="insuranceExpiry"></div>
    </div>`, async (form) => {
    await apiPost('/vehicles', {
      vehicleNumber: val(form, 'vehicleNumber'), vehicleType: val(form, 'vehicleType'),
      capacity: Number(val(form, 'capacity')),
      registrationNumber: val(form, 'registrationNumber') || null,
      insuranceExpiry: val(form, 'insuranceExpiry') || null
    });
  });
}

function openInquiryModal() {
  (async () => {
    const cust = await optionList('/customers', 'id', r => r.companyName || r.name || ('#'+r.id));
    showFormModal('New inquiry', `
      <div class="row g-2">
        <div class="col-md-6"><label class="form-label">Customer</label>
          <select class="form-select form-select-sm" name="customerId"><option value="">—</option>${cust}</select></div>
        <div class="col-md-6"><label class="form-label required">Subject</label><select class="form-select form-select-sm" name="subjectChoice" id="inquirySubjectChoice" required><option value="">Select subject…</option><option>Freight Transportation</option><option>Warehousing</option><option>Shipment Tracking</option><option>Delivery Request</option><option>Rate Enquiry</option><option>Bulk Cargo</option><option value="Others">Others</option></select><input class="form-control form-control-sm mt-2 d-none" name="customSubject" id="inquiryCustomSubject" maxlength="120" placeholder="Enter custom subject"></div>
        <div class="col-md-6"><label class="form-label required">Origin</label><input class="form-control form-control-sm" name="origin" required></div>
        <div class="col-md-6"><label class="form-label required">Destination</label><input class="form-control form-control-sm" name="destination" required></div>
        <div class="col-md-4"><label class="form-label required">Quantity</label><input type="number" min="0.01" step="0.01" class="form-control form-control-sm" name="quantity" required value="1"></div>
        <div class="col-md-8"><label class="form-label">Cargo description</label><input class="form-control form-control-sm" name="cargoDescription"></div>
      </div>`, async (form) => {
      const subjectChoice = val(form, 'subjectChoice');
      const subject = subjectChoice === 'Others' ? val(form, 'customSubject').trim() : subjectChoice;
      if (!subject) throw new Error('Please enter a subject.');
      await apiPost('/inquiries', {
        customerId: val(form, 'customerId') ? Number(val(form, 'customerId')) : null,
        subject, origin: val(form, 'origin'), destination: val(form, 'destination'),
        quantity: Number(val(form, 'quantity')), cargoDescription: val(form, 'cargoDescription') || null
      });
    });
    const subjectSelect = document.getElementById('inquirySubjectChoice');
    const customSubject = document.getElementById('inquiryCustomSubject');
    subjectSelect?.addEventListener('change', () => {
      const isOther = subjectSelect.value === 'Others';
      customSubject?.classList.toggle('d-none', !isOther);
      if (customSubject) { customSubject.required = isOther; if (!isOther) customSubject.value = ''; }
    });
  })().catch(e => toast(e.message, 'error'));
}

function openQuoteModal() {
  (async () => {
    const cust = await optionList('/customers', 'id', r => r.companyName || r.name || ('#'+r.id));
    showFormModal('New quote', `
      <div class="row g-2">
        <div class="col-md-6"><label class="form-label required">Customer</label>
          <select class="form-select form-select-sm" name="customerId" required><option value="">Select…</option>${cust}</select></div>
        <div class="col-md-3"><label class="form-label required">Origin</label><input class="form-control form-control-sm" name="origin" required></div>
        <div class="col-md-3"><label class="form-label required">Destination</label><input class="form-control form-control-sm" name="destination" required></div>
        <div class="col-md-4"><label class="form-label required">Amount</label><input type="number" min="0" step="0.01" class="form-control form-control-sm" name="amount" required></div>
        <div class="col-md-4"><label class="form-label">Valid until</label><input type="date" class="form-control form-control-sm" name="validUntil"></div>
        <div class="col-md-4"><label class="form-label">Notes</label><input class="form-control form-control-sm" name="notes"></div>
      </div>`, async (form) => {
      await apiPost('/quotes', {
        customerId: Number(val(form, 'customerId')), origin: val(form, 'origin'), destination: val(form, 'destination'),
        amount: Number(val(form, 'amount')), validUntil: val(form, 'validUntil') || null, notes: val(form, 'notes') || null
      });
    });
  })().catch(e => toast(e.message, 'error'));
}

function openBookingModal() {
  (async () => {
    const cust = await optionList('/customers', 'id', r => r.companyName || r.name || ('#'+r.id));
    const [orderRows, bookingRows] = await Promise.all([loadList('/orders'), loadList('/bookings')]);
    const bookedOrderIds = new Set(bookingRows.map(b => Number(b.orderId)).filter(Boolean));
    const eligibleOrders = orderRows.filter(o => !bookedOrderIds.has(Number(o.id)) && !['CANCELLED','DRAFT'].includes(String(o.status || '').toUpperCase()));
    const orders = eligibleOrders.map(r => `<option value="${r.id}">${escapeHtml(r.orderNumber || ('#'+r.id))}</option>`).join('');
    showFormModal('New booking', `
      <div class="row g-2">
        <div class="col-md-6"><label class="form-label required">Customer</label>
          <select class="form-select form-select-sm" name="customerId" required><option value="">Select…</option>${cust}</select></div>
        <div class="col-md-6"><label class="form-label required">Order</label>
          <select class="form-select form-select-sm" name="orderId" required><option value="">Select…</option>${orders}</select></div>
        <div class="col-md-4"><label class="form-label required">Pickup</label><input class="form-control form-control-sm" name="pickup" required></div>
        <div class="col-md-4"><label class="form-label required">Destination</label><input class="form-control form-control-sm" name="destination" required></div>
        <div class="col-md-4"><label class="form-label required">Scheduled date</label><input type="date" class="form-control form-control-sm" name="scheduledDate" required></div>
      </div>`, async (form) => {
      await apiPost('/bookings', {
        customerId: Number(val(form, 'customerId')), orderId: Number(val(form, 'orderId')),
        pickup: val(form, 'pickup'), destination: val(form, 'destination'),
        scheduledDate: val(form, 'scheduledDate')
      });
    });
  })().catch(e => toast(e.message, 'error'));
}

function openInvoiceModal() {
  (async () => {
    const cust = await optionList('/customers', 'id', r => r.companyName || r.name || ('#'+r.id));
    const orders = await optionList('/orders', 'id', r => r.orderNumber || ('#'+r.id));
    const ships = await optionList('/shipments', 'id', r => r.shipmentNumber || ('#'+r.id));
    showFormModal('New invoice', `
      <div class="row g-2">
        <div class="col-md-4"><label class="form-label required">Customer</label>
          <select class="form-select form-select-sm" name="customerId" required><option value="">Select…</option>${cust}</select></div>
        <div class="col-md-4"><label class="form-label required">Order</label>
          <select class="form-select form-select-sm" name="orderId" required><option value="">Select…</option>${orders}</select></div>
        <div class="col-md-4"><label class="form-label">Shipment</label>
          <select class="form-select form-select-sm" name="shipmentId"><option value="">—</option>${ships}</select></div>
        <div class="col-md-3"><label class="form-label required">Subtotal</label><input type="number" min="0" step="0.01" class="form-control form-control-sm" name="subtotal" required></div>
        <div class="col-md-3"><label class="form-label required">Tax</label><input type="number" min="0" step="0.01" class="form-control form-control-sm" name="tax" required value="0"></div>
        <div class="col-md-3"><label class="form-label required">Discount</label><input type="number" min="0" step="0.01" class="form-control form-control-sm" name="discount" required value="0"></div>
        <div class="col-md-3"><label class="form-label">Due date</label><input type="date" class="form-control form-control-sm" name="dueDate"></div>
      </div>`, async (form) => {
      await apiPost('/invoices', {
        customerId: Number(val(form, 'customerId')), orderId: Number(val(form, 'orderId')),
        shipmentId: val(form, 'shipmentId') ? Number(val(form, 'shipmentId')) : null,
        subtotal: Number(val(form, 'subtotal')), tax: Number(val(form, 'tax')),
        discount: Number(val(form, 'discount')), dueDate: val(form, 'dueDate') || null
      });
    });
  })().catch(e => toast(e.message, 'error'));
}

function openReceiptModal() {
  (async () => {
    const wh = await optionList('/warehouses', 'id', r => r.name || ('#'+r.id));
    const orders = await optionList('/orders', 'id', r => r.orderNumber || ('#'+r.id));
    const commodities = await optionList('/commodities', 'id', r => r.name || ('#'+r.id));
    showFormModal('New goods receipt', `
      <div class="row g-2">
        <div class="col-md-4"><label class="form-label required">Warehouse</label>
          <select class="form-select form-select-sm" name="warehouseId" required><option value="">Select…</option>${wh}</select></div>
        <div class="col-md-4"><label class="form-label required">Order</label>
          <select class="form-select form-select-sm" name="orderId" required><option value="">Select…</option>${orders}</select></div>
        <div class="col-md-4"><label class="form-label required">Commodity</label>
          <select class="form-select form-select-sm" name="commodityId" required><option value="">Select…</option>${commodities}</select></div>
        <div class="col-md-4"><label class="form-label required">Quantity</label><input type="number" min="0.01" step="0.01" class="form-control form-control-sm" name="quantity" required></div>
        <div class="col-md-8"><label class="form-label">Remarks</label><input class="form-control form-control-sm" name="remarks"></div>
      </div>`, async (form) => {
      await apiPost('/receipts', {
        warehouseId: Number(val(form, 'warehouseId')), orderId: Number(val(form, 'orderId')),
        commodityId: Number(val(form, 'commodityId')), quantity: Number(val(form, 'quantity')),
        remarks: val(form, 'remarks') || null
      });
    });
  })().catch(e => toast(e.message, 'error'));
}

function openTransferModal() {
  (async () => {
    const wh = await optionList('/warehouses', 'id', r => r.name || ('#'+r.id));
    const commodities = await optionList('/commodities', 'id', r => r.name || ('#'+r.id));
    showFormModal('Stock transfer', `
      <div class="row g-2">
        <div class="col-md-4"><label class="form-label required">From warehouse</label>
          <select class="form-select form-select-sm" name="fromWarehouseId" required><option value="">Select…</option>${wh}</select></div>
        <div class="col-md-4"><label class="form-label required">To warehouse</label>
          <select class="form-select form-select-sm" name="toWarehouseId" required><option value="">Select…</option>${wh}</select></div>
        <div class="col-md-4"><label class="form-label required">Commodity</label>
          <select class="form-select form-select-sm" name="commodityId" required><option value="">Select…</option>${commodities}</select></div>
        <div class="col-md-4"><label class="form-label required">Quantity</label><input type="number" min="0.01" step="0.01" class="form-control form-control-sm" name="quantity" required></div>
        <div class="col-md-8"><label class="form-label">Remarks</label><input class="form-control form-control-sm" name="remarks"></div>
      </div>`, async (form) => {
      const fromId = Number(val(form, 'fromWarehouseId'));
      const toId = Number(val(form, 'toWarehouseId'));
      if (fromId === toId) throw new Error('Source and destination warehouses must differ');
      await apiPost('/stock-transfers', {
        fromWarehouseId: fromId, toWarehouseId: toId,
        commodityId: Number(val(form, 'commodityId')), quantity: Number(val(form, 'quantity')),
        remarks: val(form, 'remarks') || null
      });
    });
  })().catch(e => toast(e.message, 'error'));
}

window.setCustomerStatus = async function(id, status) {
  if (!confirm('Set customer status to ' + status + '?')) return;
  try {
    const res = await api('/customers/' + id + '/status', {
      method: 'PATCH',
      body: JSON.stringify({ status })
    });
    if (!res.ok && res.status !== 204) {
      const data = await res.json().catch(() => ({}));
      throw new Error(data.message || 'Status update failed');
    }
    toast('Customer status updated');
    navigate();
  } catch (e) { toast(e.message, 'error'); }
};

window.openAssignModal = function(shipmentId) {
  (async () => {
    const drivers = await optionList('/drivers', 'id', r => (r.name || ('#'+r.id)) + ' · ' + (r.status||''));
    const vehicles = await optionList('/vehicles', 'id', r => (r.vehicleNumber || ('#'+r.id)));
    showFormModal('Assign driver / vehicle', `
      <div class="row g-2">
        <div class="col-md-6"><label class="form-label required">Driver</label>
          <select class="form-select form-select-sm" name="driverId" required><option value="">Select driver…</option>${drivers}</select></div>
        <div class="col-md-6"><label class="form-label required">Vehicle</label>
          <select class="form-select form-select-sm" name="vehicleId" required><option value="">Select vehicle…</option>${vehicles}</select></div>
      </div>`, async (form) => {
      const body = {
        driverId: val(form, 'driverId') ? Number(val(form, 'driverId')) : null,
        vehicleId: val(form, 'vehicleId') ? Number(val(form, 'vehicleId')) : null
      };
      await apiPost('/shipments/' + shipmentId + '/assign', body);
      toast('Driver assigned successfully');
      navigate();
    });
  })().catch(e => toast(e.message, 'error'));
};

window.toggleEntityStatus = async function(path, id, statusParam, value) {
  if (!confirm('Change status to ' + value + '?')) return;
  try {
    const res = await api(path + '/' + id + '/status?' + statusParam + '=' + encodeURIComponent(value), { method: 'PATCH' });
    if (!res.ok && res.status !== 204) {
      const data = await res.json().catch(() => ({}));
      throw new Error(data.message || 'Status update failed');
    }
    toast('Status updated');
    navigate();
  } catch (e) { toast(e.message, 'error'); }
};


/* —— Shell events —— */
document.getElementById('logoutBtn')?.addEventListener('click', async () => {
  try { await apiPost('/auth/logout', {}); } catch (_) {}
  if (typeof clearAuthStorage === 'function') clearAuthStorage();
  else {
    localStorage.removeItem('token'); localStorage.removeItem('user');
    sessionStorage.removeItem('token'); sessionStorage.removeItem('user');
  }
  window.location.href = '/index.html';
});
document.getElementById('menuToggle')?.addEventListener('click', () => {
  document.getElementById('sidebar')?.classList.toggle('show');
  document.getElementById('sidebarOverlay')?.classList.toggle('show');
});
document.getElementById('sidebarOverlay')?.addEventListener('click', () => {
  document.getElementById('sidebar')?.classList.remove('show');
  document.getElementById('sidebarOverlay')?.classList.remove('show');
});

buildSidebar();
window.addEventListener('hashchange', navigate);

/* Phase 11 — sidebar collapse + nav filter */
(function initShellUx() {
  const key = 'astera_sidebar_collapsed';
  if (localStorage.getItem(key) === '1') document.body.classList.add('sidebar-collapsed');
  document.getElementById('sidebarCollapseBtn')?.addEventListener('click', () => {
    document.body.classList.toggle('sidebar-collapsed');
    localStorage.setItem(key, document.body.classList.contains('sidebar-collapsed') ? '1' : '0');
  });
  document.getElementById('globalNavFilter')?.addEventListener('input', (e) => {
    const q = (e.target.value || '').trim().toLowerCase();
    document.querySelectorAll('#sidebarNav .nav-link-app').forEach(a => {
      const label = (a.textContent || '').toLowerCase();
      a.style.display = !q || label.includes(q) ? '' : 'none';
    });
    document.querySelectorAll('#sidebarNav .nav-section').forEach(sec => {
      let el = sec.nextElementSibling;
      let any = false;
      while (el && !el.classList.contains('nav-section')) {
        if (el.classList.contains('nav-link-app') && el.style.display !== 'none') any = true;
        el = el.nextElementSibling;
      }
      sec.style.display = any || !q ? '' : 'none';
    });
  });
})();

navigate();
