(function(){"use strict";async function $(t){const e=await fetch(`${t}/public/widget/config`);if(!e.ok)throw new Error("Failed to load widget config");return e.json()}async function U(t){const e=await fetch(`${t}/public/widget/events`);if(!e.ok)throw new Error("Failed to load events");return e.json()}async function j(t,e){const s=await fetch(`${t}/public/widget/bookings`,{method:"POST",headers:{"Content-Type":"application/json"},body:JSON.stringify(e)});if(!s.ok)throw new Error("Failed to create booking");return s.json()}const R="soldo-widget-styles",V={primaryColor:"#2563eb",backgroundColor:"#ffffff",textColor:"#111827",buttonTextColor:"#ffffff",borderRadius:"8px",fontFamily:"system-ui, sans-serif"};function O(t){return`
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
`}function _(){const t=document.createElement("style");t.id=R,t.textContent=O(V),document.head.appendChild(t)}function z(t){const e=document.getElementById(R);e&&(e.textContent=O(t))}function o(t,e){const s=document.createElement(t);return e&&(s.className=e),s}function D(t){return new Date(t).toLocaleDateString("ru-RU",{day:"numeric",month:"long",year:"numeric"})}function L(t){return!t||t===0?"Бесплатно":t.toLocaleString("ru-RU")+" ₽"}function H(t){const e=new Date;e.setHours(0,0,0,0);const s=new Date(t);return s.setHours(0,0,0,0),Math.round((s.getTime()-e.getTime())/864e5)}function W(t){const e=t.filter(n=>n.status==="PUBLISHED");return e.length===0?null:[...e].sort((n,i)=>new Date(n.startDate).getTime()-new Date(i.startDate).getTime())[0].id}function I(){const t=o("div","sw-loading");return t.textContent="Загрузка...",t}function G(t){const e=o("div","sw-error-block"),s=o("div");s.textContent="⚠️",s.style.fontSize="2rem",s.style.marginBottom="8px";const n=o("p");return n.textContent=t||"Произошла ошибка",e.appendChild(s),e.appendChild(n),e}function P(t,e,s){const n=t.status==="DRAFT",i=t.startDate?H(t.startDate):null,r=o("div","sw-event-card");if(e&&r.classList.add("sw-event-card--nearest"),n){const d=o("span","sw-badge sw-badge--soon");d.textContent="Скоро",r.appendChild(d)}else if(e){const d=o("span","sw-badge sw-badge--nearest");d.textContent="Ближайшее",r.appendChild(d)}const a=o("h3","sw-card-title");if(a.textContent=t.title,r.appendChild(a),t.description){const d=o("p","sw-card-desc");d.textContent=t.description,r.appendChild(d)}if(t.startDate){const d=o("div","sw-card-meta"),p=o("span","sw-meta-label");p.textContent="Дата: ";const y=o("span"),g=D(t.startDate),b=t.endDate?D(t.endDate):null;if(y.textContent=b&&b!==g?`${g} — ${b}`:g,d.appendChild(p),d.appendChild(y),r.appendChild(d),!n&&i!==null&&i>=0){const f=o("div","sw-card-days");f.textContent=i===0?"Сегодня":`Через ${i} ${q(i)}`,r.appendChild(f)}}const l=o("div","sw-card-price"),u=t.priceOptions??[];if(u.length>1){const d=Math.min(...u.map(p=>Number(p.price)));l.textContent=`от ${d.toLocaleString("ru-RU")} ₽`}else l.textContent=L(t.price);if(r.appendChild(l),!n){const d=o("button","sw-btn");d.type="button",d.textContent="Записаться",d.addEventListener("click",()=>s({type:"SELECT_EVENT",event:t})),r.appendChild(d)}return r}function q(t){const e=Math.abs(t);return e%10===1&&e%100!==11?"день":e%10>=2&&e%10<=4&&(e%100<10||e%100>=20)?"дня":"дней"}function K(t,e){const{events:s}=t,n=W(s);if(s.length===0){const u=o("div","sw-loading");return u.textContent="Нет доступных событий",u}let i=0;const r=o("div","sw-carousel"),a=o("div","sw-carousel-track"),l=s.map((u,d)=>{const p=o("div","sw-carousel-slide");return d===0&&p.classList.add("sw-carousel-slide--active"),p.appendChild(P(u,u.id===n,e)),a.appendChild(p),p});if(r.appendChild(a),s.length>1){let u=function(f){l[i].classList.remove("sw-carousel-slide--active"),y[i].classList.remove("sw-dot--active"),i=f,l[i].classList.add("sw-carousel-slide--active"),y[i].classList.add("sw-dot--active")};const d=o("div","sw-carousel-nav"),p=o("div","sw-carousel-dots"),y=[];s.forEach((f,h)=>{const v=o("button",`sw-dot${h===0?" sw-dot--active":""}`);v.type="button",v.setAttribute("aria-label",`Событие ${h+1}`),v.addEventListener("click",()=>u(h)),p.appendChild(v),y.push(v)});const g=o("button","sw-carousel-arrow");g.type="button",g.setAttribute("aria-label","Предыдущее"),g.innerHTML="&#8592;",g.addEventListener("click",()=>u((i-1+s.length)%s.length));const b=o("button","sw-carousel-arrow");b.type="button",b.setAttribute("aria-label","Следующее"),b.innerHTML="&#8594;",b.addEventListener("click",()=>u((i+1)%s.length)),d.appendChild(g),d.appendChild(p),d.appendChild(b),r.appendChild(d)}return r}function J(t,e){var v,N;const s=o("div"),n=o("button","sw-btn-back");n.type="button",n.textContent="← Назад",n.addEventListener("click",()=>e({type:"BACK"})),s.appendChild(n);let i=null;if(t.selectedEvent){const m=o("div","sw-selected-event"),x=o("div");if(x.textContent=t.selectedEvent.title,x.style.fontWeight="600",x.style.marginBottom="4px",m.appendChild(x),t.selectedEvent.startDate){const w=o("div");w.textContent=D(t.selectedEvent.startDate),w.style.opacity="0.7",m.appendChild(w)}i=o("div");const c=t.selectedEvent.priceOptions??[];if(c.length>1){const w=Math.min(...c.map(C=>Number(C.price)));i.textContent=`от ${w.toLocaleString("ru-RU")} ₽`}else i.textContent=L(t.selectedEvent.price);i.style.fontWeight="600",i.style.marginTop="4px",m.appendChild(i),s.appendChild(m)}const r=o("form");r.noValidate=!0;function a(m,x){const c=o("div","sw-form-group"),w=o("label");return w.textContent=m,c.appendChild(w),c.appendChild(x),c}const l=((v=t.selectedEvent)==null?void 0:v.priceOptions)??[];let u=l.length===1?l[0].id:void 0;if(l.length>1){const m=o("div","sw-form-group"),x=o("label");x.textContent="Вариант участия *",m.appendChild(x),l.forEach((c,w)=>{const C=o("div");C.style.cssText="display:flex;align-items:center;gap:8px;margin-bottom:6px;cursor:pointer;";const E=o("input");E.type="radio",E.name="priceOption",E.value=String(c.id),E.id=`opt-${c.id}`,w===0&&(E.checked=!0,u=c.id),E.addEventListener("change",()=>{u=c.id,i&&(i.textContent=L(c.price))});const T=o("label");T.htmlFor=`opt-${c.id}`,T.style.cssText="cursor:pointer;flex:1;",T.textContent=`${c.name} — ${L(c.price)}`,C.appendChild(E),C.appendChild(T),m.appendChild(C)}),r.appendChild(m)}const d=o("input","sw-input");d.type="text",d.autocomplete="name",d.placeholder="Иван Иванов",r.appendChild(a("Имя *",d));const p=o("input","sw-input");p.type="tel",p.autocomplete="tel",p.placeholder="+7 (999) 000-00-00",r.appendChild(a("Телефон *",p));const y=!!((N=t.selectedEvent)!=null&&N.hasDocuments),g=o("input","sw-input");g.type="email",g.autocomplete="email",g.placeholder="example@mail.ru",r.appendChild(a(y?"Email *":"Email",g));const b=o("textarea","sw-input");b.rows=3,b.style.resize="vertical",r.appendChild(a("Комментарий",b));const f=o("div","sw-error");if(f.style.display="none",r.appendChild(f),t.error){const m=o("div","sw-error");m.textContent=t.error,r.appendChild(m)}const h=o("button","sw-btn");return h.type="submit",h.textContent="Записаться",r.appendChild(h),r.addEventListener("submit",m=>{m.preventDefault();const x=d.value.trim(),c=p.value.trim(),w=g.value.trim(),C=!w||/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(w);if(!x||!c||y&&!w||!C){f.textContent=!x||!c?"Заполните обязательные поля (имя и телефон)":y&&!w?"Email обязателен для получения документов":"Введите корректный email",f.style.display="block";return}if(l.length>1&&!u){f.textContent="Выберите вариант участия",f.style.display="block";return}f.style.display="none",h.disabled=!0,h.textContent="Отправка...",e({type:"SUBMIT_FORM",name:x,phone:c,email:w,notes:b.value.trim(),priceOptionId:u})}),s.appendChild(r),s}function Y(t){var i,r;const e=o("div","sw-success"),s=o("div","sw-success-icon");s.textContent="✅",e.appendChild(s);const n=o("p");if(n.style.fontWeight="600",n.style.fontSize="1.1rem",n.textContent=((i=t.booking)==null?void 0:i.successMessage)||((r=t.config)==null?void 0:r.successMessage)||"Бронирование успешно создано!",e.appendChild(n),t.booking){const a=o("p");if(a.textContent=t.booking.eventTitle,a.style.opacity="0.7",e.appendChild(a),t.booking.amountDue>0){const l=o("p");l.textContent=L(t.booking.amountDue),l.style.fontWeight="600",e.appendChild(l)}}return e}function Q(t,e){switch(t.step){case"LOADING":return I();case"EVENTS":return K(t,e);case"FORM":return J(t,e);case"SUCCESS":return Y(t);case"ERROR":return G(t.error);default:return I()}}const S=document.currentScript??(()=>{const t=document.querySelectorAll('script[src*="widget"]');return t[t.length-1]})(),k=S.dataset.apiUrl||window.location.origin,B=S.dataset.container||null;_();function M(t){return{step:"LOADING",config:null,events:[],selectedEvent:null,booking:null,error:null,apiUrl:t}}async function X(){var a;let t=null;try{t=await $(k),z(t)}catch{}if(B){const l=document.querySelector(B);if(l){l.setAttribute("data-soldo-widget",""),F(l,M(k),t);return}}const e=(t==null?void 0:t.buttonLabel)||"Записаться",s=(t==null?void 0:t.primaryColor)||"#2563eb",n=(t==null?void 0:t.buttonTextColor)||"#ffffff",i=(t==null?void 0:t.borderRadius)||"8px",r=document.createElement("button");r.textContent=e,r.style.cssText=`
    background:${s};color:${n};border:none;
    border-radius:${i};padding:12px 28px;font-size:1rem;
    cursor:pointer;transition:opacity 0.15s;
  `,r.onmouseenter=()=>{r.style.opacity="0.85"},r.onmouseleave=()=>{r.style.opacity="1"},(a=S.parentNode)==null||a.insertBefore(r,S),r.addEventListener("click",()=>Z(t))}function Z(t){const e=document.createElement("div");e.style.cssText=`
    position:fixed;inset:0;z-index:999999;background:rgba(0,0,0,0.55);
    display:flex;align-items:center;justify-content:center;padding:16px;overflow-y:auto;
  `;const s=document.createElement("div");s.style.cssText=`
    position:relative;background:#fff;border-radius:12px;
    width:100%;max-width:600px;max-height:90vh;overflow-y:auto;
    box-shadow:0 20px 60px rgba(0,0,0,0.3);margin:auto;
  `;const n=document.createElement("button");n.textContent="✕",n.style.cssText=`
    position:sticky;top:0;float:right;background:transparent;border:none;
    font-size:1.25rem;cursor:pointer;color:#6b7280;padding:12px 16px;line-height:1;z-index:1;
  `,n.onmouseenter=()=>{n.style.color="#111827"},n.onmouseleave=()=>{n.style.color="#6b7280"};const i=()=>e.remove();n.addEventListener("click",i),e.addEventListener("click",a=>{a.target===e&&i()}),document.addEventListener("keydown",function a(l){l.key==="Escape"&&(i(),document.removeEventListener("keydown",a))});const r=document.createElement("div");r.setAttribute("data-soldo-widget",""),s.appendChild(n),s.appendChild(r),e.appendChild(s),document.body.appendChild(e),F(r,M(k),t)}async function F(t,e,s){let n=e;function i(){t.innerHTML="",t.appendChild(Q(n,r))}async function r(a){if(a.type==="SELECT_EVENT")n={...n,step:"FORM",selectedEvent:a.event},i();else if(a.type==="BACK")n={...n,step:"EVENTS",selectedEvent:null,error:null},i();else if(a.type==="SUBMIT_FORM"){n={...n,step:"LOADING"},i();try{const l=await j(k,{eventId:n.selectedEvent.id,guestName:a.name,guestPhone:a.phone,guestEmail:a.email||void 0,notes:a.notes||void 0,priceOptionId:a.priceOptionId});n={...n,step:"SUCCESS",booking:l},i()}catch{n={...n,step:"FORM",error:"Ошибка при бронировании. Попробуйте ещё раз."},i()}}}if(!s){n={...n,step:"ERROR",error:"Не удалось загрузить виджет"},i();return}n={...n,config:s,step:"LOADING"},i();try{const a=await U(k);n={...n,events:a,step:"EVENTS"},i()}catch{n={...n,step:"ERROR",error:"Не удалось загрузить события"},i()}}X();function A(){$(k).then(z).catch(()=>{})}setInterval(A,3e4),document.addEventListener("visibilitychange",()=>{document.visibilityState==="visible"&&A()})})();
