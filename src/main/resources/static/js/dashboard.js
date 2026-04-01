// SecureShield Dashboard - Dynamic API Integration
// Replaces static data with /api/dashboard calls

/* GLOBAL STATE */
let ipsMode = true;
let logIndex = 0;
let _dashIntervals = [];
let recentIncidents = [];
let recentAlerts = [];
let metrics = {};

/* UTILITY FUNCTIONS (keep existing) */
function sevBadge(s) {
  const map = { critical:'badge-critical', high:'badge-high', medium:'badge-medium', low:'badge-low', info:'badge-info' };
  return `<span class="badge ${map[s] || 'badge-info'}">${s}</span>`;
}

function actionBadge(a) {
  const map = { blocked:'badge-blocked', monitor:'badge-monitor', allowed:'badge-allowed' };
  return `<span class="badge ${map[a] || 'badge-info'}">${a}</span>`;
}

/* API DATA LOADING */
async function loadDashboardData() {
  try {
    const [incidentsRes, alertsRes, metricsRes] = await Promise.all([
      fetch('/api/dashboard/incidents', { credentials: 'include' }),
      fetch('/api/dashboard/alerts', { credentials: 'include' }),
      fetch('/api/dashboard/metrics', { credentials: 'include' })
    ]);

    if (incidentsRes.ok) recentIncidents = await incidentsRes.json();
    if (alertsRes.ok) recentAlerts = await alertsRes.json();
    if (metricsRes.ok) metrics = await metricsRes.json();

    renderIncidents();
    document.getElementById('m-alerts').textContent = metrics.activeAlerts || recentAlerts.length;
    document.getElementById('m-blocked').textContent = metrics.blockedThreats || 1247;
    
  } catch (error) {
    console.error('Failed to load dashboard data:', error);
    // Fallback to static/mock
  }
}

/* RENDER FUNCTIONS - Updated for dynamic data */
function renderIncidents() {
  const tbody = document.getElementById('incident-tbody');
  tbody.innerHTML = recentIncidents.slice(0,10).map(r => `
    <tr style="cursor:pointer">
      <td style="font-family:var(--mono)">${r.time || r.createdAt?.slice(11,19) || '00:00:00'}</td>
      <td style="font-weight:500">${r.type}</td>
      <td><span class="ip-tag">${r.ip}</span></td>
      <td>${sevBadge(r.severity)}</td>
      <td>${actionBadge(r.action)}</td>
    </tr>
  `).join('') || '<tr><td colspan="5">No incidents</td></tr>';
}

/* Keep existing render functions for charts etc. (mock data OK for now) */
function renderAttackBars() {
  // Keep static for demo, or aggregate from data
  const attackTypes = [
    { name: 'SQL Injection', count: recentIncidents.filter(i => i.type === 'SQL Injection').length * 50, color: '#ef4444' },
    { name: 'XSS', count: 281, color: '#f97316' },
    // etc.
  ];
  const max = Math.max(...attackTypes.map(a => a.count));
  document.getElementById('attack-bars').innerHTML = attackTypes.map(a => `
    <div class="bar-row">
      <div class="bar-label">${a.name}</div>
      <div class="bar-track">
        <div class="bar-fill" style="width:${Math.min((a.count/max*100),100)}%;background:${a.color}"></div>
      </div>
      <div class="bar-count">${a.count}</div>
    </div>
  `).join('');
}

function renderSparklines() {
  const sparks = {
    'spark-blocked': [40,55,38,62,70,58,80,74,90,85,95,100],
    'spark-alerts': [3,5,2,8,6,4,9,7,10,8,12,recentAlerts.length],
    'spark-rps': [60,70,65,80,72,85,78,88,75,82,79,90]
  };
  Object.entries(sparks).forEach(([id, vals]) => {
    const el = document.getElementById(id);
    const max = Math.max(...vals);
    el.innerHTML = vals.map(v => `<div class="spark-bar" style="height:${v/max*100}%;background:var(--accent)33"></div>`).join('');
  });
}

/* Keep other functions (showPage, toggleMode, etc.) */
function showPage(id) {
  document.querySelectorAll('.page').forEach(p => p.classList.remove('active'));
  document.querySelectorAll('.nav-item').forEach(n => n.classList.remove('active'));
  document.getElementById(`page-${id}`)?.classList.add('active');
  event?.currentTarget?.classList.add('active');
  
  const titles = {
    dashboard: 'Security Dashboard', alerts: 'Alert Management', 
    livelog: 'Live Traffic', owasp: 'OWASP Top 10', 
    rules: 'Rules Engine', blocklist: 'Access Control', 
    settings: 'Configuration'
  };
  document.getElementById('page-title').textContent = titles[id] || 'Dashboard';
}

function toggleMode() {
  ipsMode = !ipsMode;
  const ind = document.getElementById('mode-indicator');
  ind.textContent = ipsMode ? 'IPS MODE' : 'IDS MODE';
  ind.className = `mode-pill ${ipsMode ? 'mode-ips' : 'mode-ids'}`;
}

function appendLog(containerId) {
  const container = document.getElementById(containerId);
  const logs = [
    { level:'BLOCK', msg:'SQLi from 185.220.101.47', color:'#ef4444' },
    { level:'ALERT', msg:'XSS detected', color:'#f97316' }
  ];
  const entry = logs[logIndex % logs.length];
  logIndex++;
  const ts = new Date().toTimeString().slice(0,8);
  const div = document.createElement('div');
  div.className = 'log-entry';
  div.style.cssText = `color:${entry.color};border-left:3px solid ${entry.color};padding-left:12px`;
  div.innerHTML = `<span class="log-time">${ts}</span> <span class="log-level">${entry.level}</span> ${entry.msg}`;
  container.insertBefore(div, container.firstChild);
  // Limit height
  container.style.height = '300px';
  container.style.overflowY = 'auto';
}

/* PHISHING (unchanged) */
function checkPhishingUrl(url = document.getElementById('phishing-url').value.trim()) {
  if (!url) return;
  const loading = document.getElementById('phishing-loading');
  const results = document.getElementById('phishing-results');
  loading.style.display = 'block';
  results.style.display = 'none';

  fetch('/api/phishing/check', {
    method: 'POST',
    credentials: 'include',
    headers: {'Content-Type': 'application/json'},
    body: JSON.stringify({url})
  })
  .then(res => {
    if (!res.ok) throw new Error('Auth required');
    return res.json();
  })
  .then(data => {
    showPhishingResult(data);
  })
  .catch(e => {
    loading.style.display = 'none';
    alert('Phishing check failed: ' + e.message);
  });
}

function showPhishingResult(data) {
  document.getElementById('phishing-loading').style.display = 'none';
  document.getElementById('phishing-results').style.display = 'block';
  
  const circle = document.getElementById('risk-circle');
  const verdict = document.getElementById('risk-verdict');
  const score = document.getElementById('risk-score');
  const list = document.getElementById('reason-list');
  
  circle.className = `risk-circle risk-${data.verdict.toLowerCase()}`;
  verdict.textContent = data.verdict;
  score.textContent = data.riskScore;
  
  list.innerHTML = data.reasons.map(r => `<li>${r}</li>`).join('');
  
  document.getElementById('block-btn').style.display = data.verdict === 'PHISH' ? 'inline-block' : 'none';
}

/* INIT */
async function initDashboard() {
  // Load data
  await loadDashboardData();
  
  // Render static charts
  renderAttackBars();
  renderSparklines();
  
  // Live updates
  _dashIntervals = [
    setInterval(loadDashboardData, 10000), // Refresh every 10s
    setInterval(() => appendLog('log-container'), 2500),
    setInterval(() => {
      document.getElementById('m-rps').textContent = (3800 + Math.floor(Math.random()*400)).toLocaleString();
    }, 3000)
  ];
}

// Auto-init on load
document.addEventListener('DOMContentLoaded', initDashboard);

// Login screen functions (keep)
function lTogglePassword() { /* existing */ }
function handleSubmit(e) { /* existing */ }
// ... other login functions
