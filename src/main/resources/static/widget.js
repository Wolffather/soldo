(function(){"use strict";async function D(t,e){const o=await fetch(`${t}/public/widget/config?tenantSlug=${e}`);if(!o.ok)throw new Error("Failed to load widget config");return o.json()}async function L(t,e){const o=await fetch(`${t}/public/widget/events?tenantSlug=${e}`);if(!o.ok)throw new Error("Failed to load events");return o.json()}async function z(t,e){const o=await fetch(`${t}/public/widget/bookings`,{method:"POST",headers:{"Content-Type":"application/json"},body:JSON.stringify(e)});if(!o.ok)throw new Error("Failed to create booking");return o.json()}function R(t){return`
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
`}function r(t,e){const o=document.createElement(t);return e&&(o.className=e),o}function h(t){return new Date(t).toLocaleDateString("ru-RU",{day:"numeric",month:"long",year:"numeric"})}function v(t){return!t||t===0?"Бесплатно":t.toLocaleString("ru-RU")+" ₽"}function $(t){const e=new Date;e.setHours(0,0,0,0);const o=new Date(t);return o.setHours(0,0,0,0),Math.round((o.getTime()-e.getTime())/864e5)}function B(t){const e=t.filter(n=>n.status==="PUBLISHED");return e.length===0?null:[...e].sort((n,a)=>new Date(n.startDate).getTime()-new Date(a.startDate).getTime())[0].id}function C(){const t=r("div","sw-loading");return t.textContent="Загрузка...",t}function M(t){const e=r("div","sw-error-block"),o=r("div");o.textContent="⚠️",o.style.fontSize="2rem",o.style.marginBottom="8px";const n=r("p");return n.textContent=t||"Произошла ошибка",e.appendChild(o),e.appendChild(n),e}function I(t,e,o){const n=t.status==="DRAFT",a=t.startDate?$(t.startDate):null,s=r("div","sw-event-card");if(e&&s.classList.add("sw-event-card--nearest"),n){const i=r("span","sw-badge sw-badge--soon");i.textContent="Скоро",s.appendChild(i)}else if(e){const i=r("span","sw-badge sw-badge--nearest");i.textContent="Ближайшее",s.appendChild(i)}const d=r("h3","sw-card-title");if(d.textContent=t.title,s.appendChild(d),t.description){const i=r("p","sw-card-desc");i.textContent=t.description,s.appendChild(i)}if(t.startDate){const i=r("div","sw-card-meta"),p=r("span","sw-meta-label");p.textContent="Дата: ";const w=r("span"),c=h(t.startDate),u=t.endDate?h(t.endDate):null;if(w.textContent=u&&u!==c?`${c} — ${u}`:c,i.appendChild(p),i.appendChild(w),s.appendChild(i),!n&&a!==null&&a>=0){const g=r("div","sw-card-days");g.textContent=a===0?"Сегодня":`Через ${a} ${N(a)}`,s.appendChild(g)}}const l=r("div","sw-card-price");if(l.textContent=v(t.price),s.appendChild(l),!n){const i=r("button","sw-btn");i.type="button",i.textContent="Записаться",i.addEventListener("click",()=>o({type:"SELECT_EVENT",event:t})),s.appendChild(i)}return s}function N(t){const e=Math.abs(t);return e%10===1&&e%100!==11?"день":e%10>=2&&e%10<=4&&(e%100<10||e%100>=20)?"дня":"дней"}function O(t,e){const{events:o}=t,n=B(o);if(o.length===0){const i=r("div","sw-loading");return i.textContent="Нет доступных событий",i}let a=0;const s=r("div","sw-carousel"),d=r("div","sw-carousel-track");function l(){d.innerHTML="",o.forEach((i,p)=>{const w=r("div","sw-carousel-slide");w.style.display=p===a?"block":"none";const c=i.id===n;w.appendChild(I(i,c,e)),d.appendChild(w)}),updateNav()}if(s.appendChild(d),o.length>1){const i=r("div","sw-carousel-nav"),p=r("button","sw-carousel-arrow");p.type="button",p.setAttribute("aria-label","Предыдущее"),p.innerHTML="&#8592;",p.addEventListener("click",()=>{a=(a-1+o.length)%o.length,l()});const w=r("div","sw-carousel-dots"),c=r("button","sw-carousel-arrow");c.type="button",c.setAttribute("aria-label","Следующее"),c.innerHTML="&#8594;",c.addEventListener("click",()=>{a=(a+1)%o.length,l()}),i.appendChild(p),i.appendChild(w),i.appendChild(c),s.appendChild(i)}return l(),s}function A(t,e){const o=r("div"),n=r("button","sw-btn-back");if(n.type="button",n.textContent="← Назад",n.addEventListener("click",()=>e({type:"BACK"})),o.appendChild(n),t.selectedEvent){const u=r("div","sw-selected-event"),g=r("div");if(g.textContent=t.selectedEvent.title,g.style.fontWeight="600",g.style.marginBottom="4px",u.appendChild(g),t.selectedEvent.startDate){const m=r("div");m.textContent=h(t.selectedEvent.startDate),m.style.opacity="0.7",u.appendChild(m)}const b=r("div");b.textContent=v(t.selectedEvent.price),b.style.fontWeight="600",b.style.marginTop="4px",u.appendChild(b),o.appendChild(u)}const a=r("form");a.noValidate=!0;function s(u,g){const b=r("div","sw-form-group"),m=r("label");return m.textContent=u,b.appendChild(m),b.appendChild(g),b}const d=r("input","sw-input");d.type="text",d.autocomplete="name",d.placeholder="Иван Иванов",a.appendChild(s("Имя *",d));const l=r("input","sw-input");l.type="tel",l.autocomplete="tel",l.placeholder="+7 (999) 000-00-00",a.appendChild(s("Телефон *",l));const i=r("input","sw-input");i.type="email",i.autocomplete="email",i.placeholder="example@mail.ru",a.appendChild(s("Email",i));const p=r("textarea","sw-input");p.rows=3,p.style.resize="vertical",a.appendChild(s("Комментарий",p));const w=r("div","sw-error");if(w.style.display="none",a.appendChild(w),t.error){const u=r("div","sw-error");u.textContent=t.error,a.appendChild(u)}const c=r("button","sw-btn");return c.type="submit",c.textContent="Записаться",a.appendChild(c),a.addEventListener("submit",u=>{u.preventDefault();const g=d.value.trim(),b=l.value.trim();if(!g||!b){w.textContent="Заполните обязательные поля",w.style.display="block";return}w.style.display="none",c.disabled=!0,c.textContent="Отправка...",e({type:"SUBMIT_FORM",name:g,phone:b,email:i.value.trim(),notes:p.value.trim()})}),o.appendChild(a),o}function F(t){var a,s;const e=r("div","sw-success"),o=r("div","sw-success-icon");o.textContent="✅",e.appendChild(o);const n=r("p");if(n.style.fontWeight="600",n.style.fontSize="1.1rem",n.textContent=((a=t.booking)==null?void 0:a.successMessage)||((s=t.config)==null?void 0:s.successMessage)||"Бронирование успешно создано!",e.appendChild(n),t.booking){const d=r("p");if(d.textContent=t.booking.eventTitle,d.style.opacity="0.7",e.appendChild(d),t.booking.amountDue>0){const l=r("p");l.textContent=v(t.booking.amountDue),l.style.fontWeight="600",e.appendChild(l)}}return e}function U(t,e){switch(t.step){case"LOADING":return C();case"EVENTS":return O(t,e);case"FORM":return A(t,e);case"SUCCESS":return F(t);case"ERROR":return M(t.error);default:return C()}}const E=document.querySelectorAll("script[data-tenant]"),f=E[E.length-1],x=f.dataset.tenant||"",y=f.dataset.apiUrl||"https://api.soldo.ru",k=f.dataset.container||null;function S(t,e){return{step:"LOADING",config:null,events:[],selectedEvent:null,booking:null,error:null,apiUrl:t,tenantSlug:e}}async function j(){var d;let t=null;try{t=await D(y,x)}catch{}if(t){const l=document.createElement("style");l.textContent=R(t),document.head.appendChild(l)}if(k){const l=document.querySelector(k);if(l){l.setAttribute("data-soldo-widget",""),T(l,S(y,x),t);return}}const e=(t==null?void 0:t.buttonLabel)||"Записаться",o=(t==null?void 0:t.primaryColor)||"#2563eb",n=(t==null?void 0:t.buttonTextColor)||"#ffffff",a=(t==null?void 0:t.borderRadius)||"8px",s=document.createElement("button");s.textContent=e,s.style.cssText=`
    background:${o};color:${n};border:none;
    border-radius:${a};padding:12px 28px;font-size:1rem;
    cursor:pointer;transition:opacity 0.15s;
  `,s.onmouseenter=()=>{s.style.opacity="0.85"},s.onmouseleave=()=>{s.style.opacity="1"},(d=f.parentNode)==null||d.insertBefore(s,f),s.addEventListener("click",()=>H(t))}function H(t){const e=document.createElement("div");e.style.cssText=`
    position:fixed;inset:0;z-index:999999;background:rgba(0,0,0,0.55);
    display:flex;align-items:center;justify-content:center;padding:16px;overflow-y:auto;
  `;const o=document.createElement("div");o.style.cssText=`
    position:relative;background:#fff;border-radius:12px;
    width:100%;max-width:600px;max-height:90vh;overflow-y:auto;
    box-shadow:0 20px 60px rgba(0,0,0,0.3);margin:auto;
  `;const n=document.createElement("button");n.textContent="✕",n.style.cssText=`
    position:sticky;top:0;float:right;background:transparent;border:none;
    font-size:1.25rem;cursor:pointer;color:#6b7280;padding:12px 16px;line-height:1;z-index:1;
  `,n.onmouseenter=()=>{n.style.color="#111827"},n.onmouseleave=()=>{n.style.color="#6b7280"};const a=()=>e.remove();n.addEventListener("click",a),e.addEventListener("click",d=>{d.target===e&&a()}),document.addEventListener("keydown",function d(l){l.key==="Escape"&&(a(),document.removeEventListener("keydown",d))});const s=document.createElement("div");s.setAttribute("data-soldo-widget",""),o.appendChild(n),o.appendChild(s),e.appendChild(o),document.body.appendChild(e),T(s,S(y,x),t)}async function T(t,e,o){let n=e;function a(){t.innerHTML="",t.appendChild(U(n,s))}async function s(d){if(d.type==="SELECT_EVENT")n={...n,step:"FORM",selectedEvent:d.event},a();else if(d.type==="BACK")n={...n,step:"EVENTS",selectedEvent:null,error:null},a();else if(d.type==="SUBMIT_FORM"){n={...n,step:"LOADING"},a();try{const l=await z(y,{tenantSlug:x,eventId:n.selectedEvent.id,guestName:d.name,guestPhone:d.phone,guestEmail:d.email||void 0,notes:d.notes||void 0});n={...n,step:"SUCCESS",booking:l},a()}catch{n={...n,step:"FORM",error:"Ошибка при бронировании. Попробуйте ещё раз."},a()}}}if(!o){n={...n,step:"ERROR",error:"Не удалось загрузить виджет"},a();return}n={...n,config:o,step:"LOADING"},a();try{const d=await L(y,x);n={...n,events:d,step:"EVENTS"},a()}catch{n={...n,step:"ERROR",error:"Не удалось загрузить события"},a()}}j()})();
