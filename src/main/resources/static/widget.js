(function(){"use strict";async function C(t){const e=await fetch(`${t}/public/widget/config`);if(!e.ok)throw new Error("Failed to load widget config");return e.json()}async function $(t){const e=await fetch(`${t}/public/widget/events`);if(!e.ok)throw new Error("Failed to load events");return e.json()}async function B(t,e){const s=await fetch(`${t}/public/widget/bookings`,{method:"POST",headers:{"Content-Type":"application/json"},body:JSON.stringify(e)});if(!s.ok)throw new Error("Failed to create booking");return s.json()}const E="soldo-widget-styles",I={primaryColor:"#2563eb",backgroundColor:"#ffffff",textColor:"#111827",buttonTextColor:"#ffffff",borderRadius:"8px",fontFamily:"system-ui, sans-serif"};function k(t){return`
[data-soldo-widget] {
  --sw-primary: ${t.primaryColor};
  --sw-bg: ${t.backgroundColor};
  --sw-text: ${t.textColor};
  --sw-btn-text: ${t.buttonTextColor};
  --sw-radius: ${t.borderRadius};
  --sw-font: ${t.fontFamily};
  font-family: var(--sw-font);
  color: var(--sw-text);
  background: var(--sw-bg);
  padding: 24px;
  box-sizing: border-box;
}
[data-soldo-widget] * { box-sizing: border-box; }

/* ── Carousel ── */
[data-soldo-widget] .sw-carousel { width: 100%; }
[data-soldo-widget] .sw-carousel-track {
  display: grid;
  grid-template-columns: 1fr;
  grid-template-rows: 1fr;
}
[data-soldo-widget] .sw-carousel-slide {
  grid-column: 1;
  grid-row: 1;
  opacity: 0;
  pointer-events: none;
  transition: opacity 0.35s ease;
}
[data-soldo-widget] .sw-carousel-slide--active {
  opacity: 1;
  pointer-events: auto;
}
[data-soldo-widget] .sw-carousel-nav {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12px;
  margin-top: 16px;
}
[data-soldo-widget] .sw-carousel-arrow {
  background: transparent;
  border: 2px solid var(--sw-primary);
  color: var(--sw-primary);
  border-radius: 50%;
  width: 36px; height: 36px;
  font-size: 1rem;
  cursor: pointer;
  display: flex; align-items: center; justify-content: center;
  transition: background 0.15s, color 0.15s;
  flex-shrink: 0;
}
[data-soldo-widget] .sw-carousel-arrow:hover {
  background: var(--sw-primary);
  color: var(--sw-btn-text);
}
[data-soldo-widget] .sw-carousel-dots { display: flex; gap: 8px; align-items: center; }
[data-soldo-widget] .sw-dot {
  width: 8px; height: 8px;
  border-radius: 50%;
  border: none;
  background: #d1d5db;
  cursor: pointer;
  padding: 0;
  transition: background 0.15s, transform 0.15s;
}
[data-soldo-widget] .sw-dot--active {
  background: var(--sw-primary);
  transform: scale(1.3);
}

/* ── Event card ── */
[data-soldo-widget] .sw-event-card {
  border: 2px solid #e5e7eb;
  border-radius: var(--sw-radius);
  padding: 20px;
  position: relative;
  background: var(--sw-bg);
  transition: border-color 0.15s;
}
[data-soldo-widget] .sw-event-card--nearest {
  border-color: var(--sw-primary);
}

/* ── Badges ── */
[data-soldo-widget] .sw-badge {
  display: inline-block;
  font-size: 0.75rem;
  font-weight: 600;
  padding: 3px 10px;
  border-radius: 20px;
  margin-bottom: 10px;
  letter-spacing: 0.02em;
  text-transform: uppercase;
}
[data-soldo-widget] .sw-badge--nearest {
  background: var(--sw-primary);
  color: var(--sw-btn-text);
}
[data-soldo-widget] .sw-badge--soon {
  background: #f3f4f6;
  color: #6b7280;
  border: 1px solid #e5e7eb;
}

/* ── Card content ── */
[data-soldo-widget] .sw-card-title {
  margin: 0 0 8px;
  font-size: 1.15rem;
  font-weight: 700;
  color: var(--sw-text);
}
[data-soldo-widget] .sw-card-desc {
  margin: 0 0 12px;
  font-size: 0.875rem;
  opacity: 0.75;
  line-height: 1.5;
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
[data-soldo-widget] .sw-card-meta {
  font-size: 0.875rem;
  margin-bottom: 4px;
}
[data-soldo-widget] .sw-meta-label { opacity: 0.6; }
[data-soldo-widget] .sw-card-days {
  font-size: 0.8rem;
  color: var(--sw-primary);
  font-weight: 600;
  margin-bottom: 10px;
}
[data-soldo-widget] .sw-card-price {
  font-size: 1.1rem;
  font-weight: 700;
  color: var(--sw-primary);
  margin: 12px 0 16px;
}

/* ── Buttons ── */
[data-soldo-widget] .sw-btn {
  background: var(--sw-primary);
  color: var(--sw-btn-text);
  border: none;
  border-radius: var(--sw-radius);
  padding: 12px 24px;
  font-size: 1rem;
  cursor: pointer;
  width: 100%;
  transition: opacity 0.15s;
  font-weight: 600;
}
[data-soldo-widget] .sw-btn:hover { opacity: 0.85; }
[data-soldo-widget] .sw-btn:disabled { opacity: 0.5; cursor: not-allowed; }
[data-soldo-widget] .sw-btn-back {
  background: transparent;
  color: var(--sw-text);
  border: none;
  cursor: pointer;
  font-size: 0.875rem;
  padding: 0;
  margin-bottom: 16px;
  opacity: 0.7;
}
[data-soldo-widget] .sw-btn-back:hover { opacity: 1; }

/* ── Form ── */
[data-soldo-widget] .sw-form-group { margin-bottom: 12px; }
[data-soldo-widget] .sw-form-group label { display: block; font-size: 0.875rem; margin-bottom: 4px; }
[data-soldo-widget] .sw-input {
  width: 100%;
  padding: 10px 12px;
  border: 1px solid #d1d5db;
  border-radius: var(--sw-radius);
  font-size: 1rem;
  background: var(--sw-bg);
  color: var(--sw-text);
}
[data-soldo-widget] .sw-input:focus { outline: 2px solid var(--sw-primary); border-color: transparent; }
[data-soldo-widget] .sw-selected-event {
  background: #f9fafb;
  border-radius: var(--sw-radius);
  padding: 12px 16px;
  margin-bottom: 16px;
  font-size: 0.875rem;
}

/* ── States ── */
[data-soldo-widget] .sw-success { text-align: center; padding: 32px 16px; }
[data-soldo-widget] .sw-success-icon { font-size: 3rem; margin-bottom: 12px; }
[data-soldo-widget] .sw-error { color: #dc2626; font-size: 0.875rem; margin-top: 8px; }
[data-soldo-widget] .sw-error-block { text-align: center; padding: 32px 16px; opacity: 0.7; }
[data-soldo-widget] .sw-loading { text-align: center; padding: 32px; opacity: 0.6; }

${t.customCss||""}
`}function M(){const t=document.createElement("style");t.id=E,t.textContent=k(I),document.head.appendChild(t)}function L(t){const e=document.getElementById(E);e&&(e.textContent=k(t))}function o(t,e){const s=document.createElement(t);return e&&(s.className=e),s}function v(t){return new Date(t).toLocaleDateString("ru-RU",{day:"numeric",month:"long",year:"numeric"})}function h(t){return!t||t===0?"Бесплатно":t.toLocaleString("ru-RU")+" ₽"}function A(t){const e=new Date;e.setHours(0,0,0,0);const s=new Date(t);return s.setHours(0,0,0,0),Math.round((s.getTime()-e.getTime())/864e5)}function F(t){const e=t.filter(n=>n.status==="PUBLISHED");return e.length===0?null:[...e].sort((n,r)=>new Date(n.startDate).getTime()-new Date(r.startDate).getTime())[0].id}function S(){const t=o("div","sw-loading");return t.textContent="Загрузка...",t}function O(t){const e=o("div","sw-error-block"),s=o("div");s.textContent="⚠️",s.style.fontSize="2rem",s.style.marginBottom="8px";const n=o("p");return n.textContent=t||"Произошла ошибка",e.appendChild(s),e.appendChild(n),e}function N(t,e,s){const n=t.status==="DRAFT",r=t.startDate?A(t.startDate):null,a=o("div","sw-event-card");if(e&&a.classList.add("sw-event-card--nearest"),n){const d=o("span","sw-badge sw-badge--soon");d.textContent="Скоро",a.appendChild(d)}else if(e){const d=o("span","sw-badge sw-badge--nearest");d.textContent="Ближайшее",a.appendChild(d)}const i=o("h3","sw-card-title");if(i.textContent=t.title,a.appendChild(i),t.description){const d=o("p","sw-card-desc");d.textContent=t.description,a.appendChild(d)}if(t.startDate){const d=o("div","sw-card-meta"),w=o("span","sw-meta-label");w.textContent="Дата: ";const u=o("span"),b=v(t.startDate),c=t.endDate?v(t.endDate):null;if(u.textContent=c&&c!==b?`${b} — ${c}`:b,d.appendChild(w),d.appendChild(u),a.appendChild(d),!n&&r!==null&&r>=0){const p=o("div","sw-card-days");p.textContent=r===0?"Сегодня":`Через ${r} ${U(r)}`,a.appendChild(p)}}const l=o("div","sw-card-price");if(l.textContent=h(t.price),a.appendChild(l),!n){const d=o("button","sw-btn");d.type="button",d.textContent="Записаться",d.addEventListener("click",()=>s({type:"SELECT_EVENT",event:t})),a.appendChild(d)}return a}function U(t){const e=Math.abs(t);return e%10===1&&e%100!==11?"день":e%10>=2&&e%10<=4&&(e%100<10||e%100>=20)?"дня":"дней"}function j(t,e){const{events:s}=t,n=F(s);if(s.length===0){const d=o("div","sw-loading");return d.textContent="Нет доступных событий",d}let r=0;const a=o("div","sw-carousel"),i=o("div","sw-carousel-track"),l=s.map((d,w)=>{const u=o("div","sw-carousel-slide");return w===0&&u.classList.add("sw-carousel-slide--active"),u.appendChild(N(d,d.id===n,e)),i.appendChild(u),u});if(a.appendChild(i),s.length>1){let d=function(g){l[r].classList.remove("sw-carousel-slide--active"),b[r].classList.remove("sw-dot--active"),r=g,l[r].classList.add("sw-carousel-slide--active"),b[r].classList.add("sw-dot--active")};const w=o("div","sw-carousel-nav"),u=o("div","sw-carousel-dots"),b=[];s.forEach((g,m)=>{const x=o("button",`sw-dot${m===0?" sw-dot--active":""}`);x.type="button",x.setAttribute("aria-label",`Событие ${m+1}`),x.addEventListener("click",()=>d(m)),u.appendChild(x),b.push(x)});const c=o("button","sw-carousel-arrow");c.type="button",c.setAttribute("aria-label","Предыдущее"),c.innerHTML="&#8592;",c.addEventListener("click",()=>d((r-1+s.length)%s.length));const p=o("button","sw-carousel-arrow");p.type="button",p.setAttribute("aria-label","Следующее"),p.innerHTML="&#8594;",p.addEventListener("click",()=>d((r+1)%s.length)),w.appendChild(c),w.appendChild(u),w.appendChild(p),a.appendChild(w)}return a}function V(t,e){const s=o("div"),n=o("button","sw-btn-back");if(n.type="button",n.textContent="← Назад",n.addEventListener("click",()=>e({type:"BACK"})),s.appendChild(n),t.selectedEvent){const c=o("div","sw-selected-event"),p=o("div");if(p.textContent=t.selectedEvent.title,p.style.fontWeight="600",p.style.marginBottom="4px",c.appendChild(p),t.selectedEvent.startDate){const m=o("div");m.textContent=v(t.selectedEvent.startDate),m.style.opacity="0.7",c.appendChild(m)}const g=o("div");g.textContent=h(t.selectedEvent.price),g.style.fontWeight="600",g.style.marginTop="4px",c.appendChild(g),s.appendChild(c)}const r=o("form");r.noValidate=!0;function a(c,p){const g=o("div","sw-form-group"),m=o("label");return m.textContent=c,g.appendChild(m),g.appendChild(p),g}const i=o("input","sw-input");i.type="text",i.autocomplete="name",i.placeholder="Иван Иванов",r.appendChild(a("Имя *",i));const l=o("input","sw-input");l.type="tel",l.autocomplete="tel",l.placeholder="+7 (999) 000-00-00",r.appendChild(a("Телефон *",l));const d=o("input","sw-input");d.type="email",d.autocomplete="email",d.placeholder="example@mail.ru",r.appendChild(a("Email",d));const w=o("textarea","sw-input");w.rows=3,w.style.resize="vertical",r.appendChild(a("Комментарий",w));const u=o("div","sw-error");if(u.style.display="none",r.appendChild(u),t.error){const c=o("div","sw-error");c.textContent=t.error,r.appendChild(c)}const b=o("button","sw-btn");return b.type="submit",b.textContent="Записаться",r.appendChild(b),r.addEventListener("submit",c=>{c.preventDefault();const p=i.value.trim(),g=l.value.trim();if(!p||!g){u.textContent="Заполните обязательные поля",u.style.display="block";return}u.style.display="none",b.disabled=!0,b.textContent="Отправка...",e({type:"SUBMIT_FORM",name:p,phone:g,email:d.value.trim(),notes:w.value.trim()})}),s.appendChild(r),s}function _(t){var r,a;const e=o("div","sw-success"),s=o("div","sw-success-icon");s.textContent="✅",e.appendChild(s);const n=o("p");if(n.style.fontWeight="600",n.style.fontSize="1.1rem",n.textContent=((r=t.booking)==null?void 0:r.successMessage)||((a=t.config)==null?void 0:a.successMessage)||"Бронирование успешно создано!",e.appendChild(n),t.booking){const i=o("p");if(i.textContent=t.booking.eventTitle,i.style.opacity="0.7",e.appendChild(i),t.booking.amountDue>0){const l=o("p");l.textContent=h(t.booking.amountDue),l.style.fontWeight="600",e.appendChild(l)}}return e}function H(t,e){switch(t.step){case"LOADING":return S();case"EVENTS":return j(t,e);case"FORM":return V(t,e);case"SUCCESS":return _(t);case"ERROR":return O(t.error);default:return S()}}const y=document.currentScript??(()=>{const t=document.querySelectorAll('script[src*="widget"]');return t[t.length-1]})(),f=y.dataset.apiUrl||window.location.origin,T=y.dataset.container||null;M();function D(t){return{step:"LOADING",config:null,events:[],selectedEvent:null,booking:null,error:null,apiUrl:t}}async function W(){var i;let t=null;try{t=await C(f),L(t)}catch{}if(T){const l=document.querySelector(T);if(l){l.setAttribute("data-soldo-widget",""),z(l,D(f),t);return}}const e=(t==null?void 0:t.buttonLabel)||"Записаться",s=(t==null?void 0:t.primaryColor)||"#2563eb",n=(t==null?void 0:t.buttonTextColor)||"#ffffff",r=(t==null?void 0:t.borderRadius)||"8px",a=document.createElement("button");a.textContent=e,a.style.cssText=`
    background:${s};color:${n};border:none;
    border-radius:${r};padding:12px 28px;font-size:1rem;
    cursor:pointer;transition:opacity 0.15s;
  `,a.onmouseenter=()=>{a.style.opacity="0.85"},a.onmouseleave=()=>{a.style.opacity="1"},(i=y.parentNode)==null||i.insertBefore(a,y),a.addEventListener("click",()=>G(t))}function G(t){const e=document.createElement("div");e.style.cssText=`
    position:fixed;inset:0;z-index:999999;background:rgba(0,0,0,0.55);
    display:flex;align-items:center;justify-content:center;padding:16px;overflow-y:auto;
  `;const s=document.createElement("div");s.style.cssText=`
    position:relative;background:#fff;border-radius:12px;
    width:100%;max-width:600px;max-height:90vh;overflow-y:auto;
    box-shadow:0 20px 60px rgba(0,0,0,0.3);margin:auto;
  `;const n=document.createElement("button");n.textContent="✕",n.style.cssText=`
    position:sticky;top:0;float:right;background:transparent;border:none;
    font-size:1.25rem;cursor:pointer;color:#6b7280;padding:12px 16px;line-height:1;z-index:1;
  `,n.onmouseenter=()=>{n.style.color="#111827"},n.onmouseleave=()=>{n.style.color="#6b7280"};const r=()=>e.remove();n.addEventListener("click",r),e.addEventListener("click",i=>{i.target===e&&r()}),document.addEventListener("keydown",function i(l){l.key==="Escape"&&(r(),document.removeEventListener("keydown",i))});const a=document.createElement("div");a.setAttribute("data-soldo-widget",""),s.appendChild(n),s.appendChild(a),e.appendChild(s),document.body.appendChild(e),z(a,D(f),t)}async function z(t,e,s){let n=e;function r(){t.innerHTML="",t.appendChild(H(n,a))}async function a(i){if(i.type==="SELECT_EVENT")n={...n,step:"FORM",selectedEvent:i.event},r();else if(i.type==="BACK")n={...n,step:"EVENTS",selectedEvent:null,error:null},r();else if(i.type==="SUBMIT_FORM"){n={...n,step:"LOADING"},r();try{const l=await B(f,{eventId:n.selectedEvent.id,guestName:i.name,guestPhone:i.phone,guestEmail:i.email||void 0,notes:i.notes||void 0});n={...n,step:"SUCCESS",booking:l},r()}catch{n={...n,step:"FORM",error:"Ошибка при бронировании. Попробуйте ещё раз."},r()}}}if(!s){n={...n,step:"ERROR",error:"Не удалось загрузить виджет"},r();return}n={...n,config:s,step:"LOADING"},r();try{const i=await $(f);n={...n,events:i,step:"EVENTS"},r()}catch{n={...n,step:"ERROR",error:"Не удалось загрузить события"},r()}}W();function R(){C(f).then(L).catch(()=>{})}setInterval(R,3e4),document.addEventListener("visibilitychange",()=>{document.visibilityState==="visible"&&R()})})();
