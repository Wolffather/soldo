(function(){"use strict";async function D(t){const e=await fetch(`${t}/public/widget/config`);if(!e.ok)throw new Error("Failed to load widget config");return e.json()}async function S(t){const e=await fetch(`${t}/public/widget/events`);if(!e.ok)throw new Error("Failed to load events");return e.json()}async function z(t,e){const a=await fetch(`${t}/public/widget/bookings`,{method:"POST",headers:{"Content-Type":"application/json"},body:JSON.stringify(e)});if(!a.ok)throw new Error("Failed to create booking");return a.json()}const v="soldo-widget-styles",R={primaryColor:"#2563eb",backgroundColor:"#ffffff",textColor:"#111827",buttonTextColor:"#ffffff",borderRadius:"8px",fontFamily:"system-ui, sans-serif"};function C(t){return`
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
[data-soldo-widget] .sw-carousel-track { width: 100%; }
[data-soldo-widget] .sw-carousel-slide { width: 100%; }
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
`}function $(){const t=document.createElement("style");t.id=v,t.textContent=C(R),document.head.appendChild(t)}function B(t){const e=document.getElementById(v);e&&(e.textContent=C(t))}function o(t,e){const a=document.createElement(t);return e&&(a.className=e),a}function y(t){return new Date(t).toLocaleDateString("ru-RU",{day:"numeric",month:"long",year:"numeric"})}function h(t){return!t||t===0?"Бесплатно":t.toLocaleString("ru-RU")+" ₽"}function I(t){const e=new Date;e.setHours(0,0,0,0);const a=new Date(t);return a.setHours(0,0,0,0),Math.round((a.getTime()-e.getTime())/864e5)}function M(t){const e=t.filter(n=>n.status==="PUBLISHED");return e.length===0?null:[...e].sort((n,r)=>new Date(n.startDate).getTime()-new Date(r.startDate).getTime())[0].id}function E(){const t=o("div","sw-loading");return t.textContent="Загрузка...",t}function A(t){const e=o("div","sw-error-block"),a=o("div");a.textContent="⚠️",a.style.fontSize="2rem",a.style.marginBottom="8px";const n=o("p");return n.textContent=t||"Произошла ошибка",e.appendChild(a),e.appendChild(n),e}function F(t,e,a){const n=t.status==="DRAFT",r=t.startDate?I(t.startDate):null,s=o("div","sw-event-card");if(e&&s.classList.add("sw-event-card--nearest"),n){const i=o("span","sw-badge sw-badge--soon");i.textContent="Скоро",s.appendChild(i)}else if(e){const i=o("span","sw-badge sw-badge--nearest");i.textContent="Ближайшее",s.appendChild(i)}const d=o("h3","sw-card-title");if(d.textContent=t.title,s.appendChild(d),t.description){const i=o("p","sw-card-desc");i.textContent=t.description,s.appendChild(i)}if(t.startDate){const i=o("div","sw-card-meta"),p=o("span","sw-meta-label");p.textContent="Дата: ";const u=o("span"),w=y(t.startDate),l=t.endDate?y(t.endDate):null;if(u.textContent=l&&l!==w?`${w} — ${l}`:w,i.appendChild(p),i.appendChild(u),s.appendChild(i),!n&&r!==null&&r>=0){const b=o("div","sw-card-days");b.textContent=r===0?"Сегодня":`Через ${r} ${N(r)}`,s.appendChild(b)}}const c=o("div","sw-card-price");if(c.textContent=h(t.price),s.appendChild(c),!n){const i=o("button","sw-btn");i.type="button",i.textContent="Записаться",i.addEventListener("click",()=>a({type:"SELECT_EVENT",event:t})),s.appendChild(i)}return s}function N(t){const e=Math.abs(t);return e%10===1&&e%100!==11?"день":e%10>=2&&e%10<=4&&(e%100<10||e%100>=20)?"дня":"дней"}function O(t,e){const{events:a}=t,n=M(a);if(a.length===0){const p=o("div","sw-loading");return p.textContent="Нет доступных событий",p}let r=0;const s=o("div","sw-carousel"),d=o("div","sw-carousel-track");let c=()=>{};function i(){d.innerHTML="",a.forEach((p,u)=>{const w=o("div","sw-carousel-slide");w.style.display=u===r?"block":"none";const l=p.id===n;w.appendChild(F(p,l,e)),d.appendChild(w)}),c()}if(s.appendChild(d),a.length>1){const p=o("div","sw-carousel-nav"),u=o("button","sw-carousel-arrow");u.type="button",u.setAttribute("aria-label","Предыдущее"),u.innerHTML="&#8592;",u.addEventListener("click",()=>{r=(r-1+a.length)%a.length,i()});const w=o("div","sw-carousel-dots"),l=o("button","sw-carousel-arrow");l.type="button",l.setAttribute("aria-label","Следующее"),l.innerHTML="&#8594;",l.addEventListener("click",()=>{r=(r+1)%a.length,i()}),p.appendChild(u),p.appendChild(w),p.appendChild(l),s.appendChild(p),c=()=>{w.innerHTML="",a.forEach((b,g)=>{const m=o("button",`sw-dot${g===r?" sw-dot--active":""}`);m.type="button",m.setAttribute("aria-label",`Событие ${g+1}`),m.addEventListener("click",()=>{r=g,i()}),w.appendChild(m)}),u.disabled=!1,l.disabled=!1}}return i(),s}function U(t,e){const a=o("div"),n=o("button","sw-btn-back");if(n.type="button",n.textContent="← Назад",n.addEventListener("click",()=>e({type:"BACK"})),a.appendChild(n),t.selectedEvent){const l=o("div","sw-selected-event"),b=o("div");if(b.textContent=t.selectedEvent.title,b.style.fontWeight="600",b.style.marginBottom="4px",l.appendChild(b),t.selectedEvent.startDate){const m=o("div");m.textContent=y(t.selectedEvent.startDate),m.style.opacity="0.7",l.appendChild(m)}const g=o("div");g.textContent=h(t.selectedEvent.price),g.style.fontWeight="600",g.style.marginTop="4px",l.appendChild(g),a.appendChild(l)}const r=o("form");r.noValidate=!0;function s(l,b){const g=o("div","sw-form-group"),m=o("label");return m.textContent=l,g.appendChild(m),g.appendChild(b),g}const d=o("input","sw-input");d.type="text",d.autocomplete="name",d.placeholder="Иван Иванов",r.appendChild(s("Имя *",d));const c=o("input","sw-input");c.type="tel",c.autocomplete="tel",c.placeholder="+7 (999) 000-00-00",r.appendChild(s("Телефон *",c));const i=o("input","sw-input");i.type="email",i.autocomplete="email",i.placeholder="example@mail.ru",r.appendChild(s("Email",i));const p=o("textarea","sw-input");p.rows=3,p.style.resize="vertical",r.appendChild(s("Комментарий",p));const u=o("div","sw-error");if(u.style.display="none",r.appendChild(u),t.error){const l=o("div","sw-error");l.textContent=t.error,r.appendChild(l)}const w=o("button","sw-btn");return w.type="submit",w.textContent="Записаться",r.appendChild(w),r.addEventListener("submit",l=>{l.preventDefault();const b=d.value.trim(),g=c.value.trim();if(!b||!g){u.textContent="Заполните обязательные поля",u.style.display="block";return}u.style.display="none",w.disabled=!0,w.textContent="Отправка...",e({type:"SUBMIT_FORM",name:b,phone:g,email:i.value.trim(),notes:p.value.trim()})}),a.appendChild(r),a}function j(t){var r,s;const e=o("div","sw-success"),a=o("div","sw-success-icon");a.textContent="✅",e.appendChild(a);const n=o("p");if(n.style.fontWeight="600",n.style.fontSize="1.1rem",n.textContent=((r=t.booking)==null?void 0:r.successMessage)||((s=t.config)==null?void 0:s.successMessage)||"Бронирование успешно создано!",e.appendChild(n),t.booking){const d=o("p");if(d.textContent=t.booking.eventTitle,d.style.opacity="0.7",e.appendChild(d),t.booking.amountDue>0){const c=o("p");c.textContent=h(t.booking.amountDue),c.style.fontWeight="600",e.appendChild(c)}}return e}function H(t,e){switch(t.step){case"LOADING":return E();case"EVENTS":return O(t,e);case"FORM":return U(t,e);case"SUCCESS":return j(t);case"ERROR":return A(t.error);default:return E()}}const x=document.currentScript??(()=>{const t=document.querySelectorAll('script[src*="widget"]');return t[t.length-1]})(),f=x.dataset.apiUrl||window.location.origin,k=x.dataset.container||null;$();function T(t){return{step:"LOADING",config:null,events:[],selectedEvent:null,booking:null,error:null,apiUrl:t}}async function V(){var d;let t=null;try{t=await D(f),B(t)}catch{}if(k){const c=document.querySelector(k);if(c){c.setAttribute("data-soldo-widget",""),L(c,T(f),t);return}}const e=(t==null?void 0:t.buttonLabel)||"Записаться",a=(t==null?void 0:t.primaryColor)||"#2563eb",n=(t==null?void 0:t.buttonTextColor)||"#ffffff",r=(t==null?void 0:t.borderRadius)||"8px",s=document.createElement("button");s.textContent=e,s.style.cssText=`
    background:${a};color:${n};border:none;
    border-radius:${r};padding:12px 28px;font-size:1rem;
    cursor:pointer;transition:opacity 0.15s;
  `,s.onmouseenter=()=>{s.style.opacity="0.85"},s.onmouseleave=()=>{s.style.opacity="1"},(d=x.parentNode)==null||d.insertBefore(s,x),s.addEventListener("click",()=>_(t))}function _(t){const e=document.createElement("div");e.style.cssText=`
    position:fixed;inset:0;z-index:999999;background:rgba(0,0,0,0.55);
    display:flex;align-items:center;justify-content:center;padding:16px;overflow-y:auto;
  `;const a=document.createElement("div");a.style.cssText=`
    position:relative;background:#fff;border-radius:12px;
    width:100%;max-width:600px;max-height:90vh;overflow-y:auto;
    box-shadow:0 20px 60px rgba(0,0,0,0.3);margin:auto;
  `;const n=document.createElement("button");n.textContent="✕",n.style.cssText=`
    position:sticky;top:0;float:right;background:transparent;border:none;
    font-size:1.25rem;cursor:pointer;color:#6b7280;padding:12px 16px;line-height:1;z-index:1;
  `,n.onmouseenter=()=>{n.style.color="#111827"},n.onmouseleave=()=>{n.style.color="#6b7280"};const r=()=>e.remove();n.addEventListener("click",r),e.addEventListener("click",d=>{d.target===e&&r()}),document.addEventListener("keydown",function d(c){c.key==="Escape"&&(r(),document.removeEventListener("keydown",d))});const s=document.createElement("div");s.setAttribute("data-soldo-widget",""),a.appendChild(n),a.appendChild(s),e.appendChild(a),document.body.appendChild(e),L(s,T(f),t)}async function L(t,e,a){let n=e;function r(){t.innerHTML="",t.appendChild(H(n,s))}async function s(d){if(d.type==="SELECT_EVENT")n={...n,step:"FORM",selectedEvent:d.event},r();else if(d.type==="BACK")n={...n,step:"EVENTS",selectedEvent:null,error:null},r();else if(d.type==="SUBMIT_FORM"){n={...n,step:"LOADING"},r();try{const c=await z(f,{eventId:n.selectedEvent.id,guestName:d.name,guestPhone:d.phone,guestEmail:d.email||void 0,notes:d.notes||void 0});n={...n,step:"SUCCESS",booking:c},r()}catch{n={...n,step:"FORM",error:"Ошибка при бронировании. Попробуйте ещё раз."},r()}}}if(!a){n={...n,step:"ERROR",error:"Не удалось загрузить виджет"},r();return}n={...n,config:a,step:"LOADING"},r();try{const d=await S(f);n={...n,events:d,step:"EVENTS"},r()}catch{n={...n,step:"ERROR",error:"Не удалось загрузить события"},r()}}V()})();
