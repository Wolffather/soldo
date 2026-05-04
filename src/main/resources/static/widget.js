(function(){"use strict";async function D(t,a){const n=await fetch(`${t}/public/widget/config?tenantSlug=${a}`);if(!n.ok)throw new Error("Failed to load widget config");return n.json()}async function G(t,a){const n=await fetch(`${t}/public/widget/categories?tenantSlug=${a}`);if(!n.ok)throw new Error("Failed to load categories");return n.json()}async function O(t,a,n){let e=`${t}/public/widget/events?tenantSlug=${a}`;n&&(e+=`&categoryId=${n}`);const r=await fetch(e);if(!r.ok)throw new Error("Failed to load events");return r.json()}async function U(t,a){const n=await fetch(`${t}/public/widget/bookings`,{method:"POST",headers:{"Content-Type":"application/json"},body:JSON.stringify(a)});if(!n.ok)throw new Error("Failed to create booking");return n.json()}function V(t){return`
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
  border-radius: var(--sw-radius);
  padding: 24px;
  box-sizing: border-box;
}
[data-soldo-widget] * { box-sizing: border-box; }
[data-soldo-widget] h2 { margin: 0 0 16px; font-size: 1.25rem; font-weight: 600; }
[data-soldo-widget] .sw-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(160px, 1fr)); gap: 12px; }
[data-soldo-widget] .sw-card {
  border: 2px solid var(--sw-primary);
  border-radius: var(--sw-radius);
  padding: 16px;
  cursor: pointer;
  transition: background 0.15s;
  background: transparent;
  text-align: left;
  width: 100%;
  color: var(--sw-text);
}
[data-soldo-widget] .sw-card:hover { background: var(--sw-primary); color: var(--sw-btn-text); }
[data-soldo-widget] .sw-event-card {
  border: 1px solid #e5e7eb;
  border-radius: var(--sw-radius);
  padding: 16px;
  margin-bottom: 10px;
  cursor: pointer;
  transition: border-color 0.15s;
  background: transparent;
  text-align: left;
  width: 100%;
  color: var(--sw-text);
}
[data-soldo-widget] .sw-event-card:hover { border-color: var(--sw-primary); }
[data-soldo-widget] .sw-event-title { font-weight: 600; margin-bottom: 4px; }
[data-soldo-widget] .sw-event-meta { font-size: 0.875rem; color: #6b7280; }
[data-soldo-widget] .sw-price { font-weight: 600; color: var(--sw-primary); margin-top: 6px; }
[data-soldo-widget] .sw-btn {
  background: var(--sw-primary);
  color: var(--sw-btn-text);
  border: none;
  border-radius: var(--sw-radius);
  padding: 12px 24px;
  font-size: 1rem;
  cursor: pointer;
  width: 100%;
  margin-top: 12px;
  transition: opacity 0.15s;
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
[data-soldo-widget] .sw-success {
  text-align: center;
  padding: 32px 16px;
}
[data-soldo-widget] .sw-success-icon { font-size: 3rem; margin-bottom: 12px; }
[data-soldo-widget] .sw-error { color: #dc2626; font-size: 0.875rem; margin-top: 8px; }
[data-soldo-widget] .sw-loading { text-align: center; padding: 32px; opacity: 0.6; }
${t.customCss||""}
`}const p={loading:"Загрузка...",chooseCategory:"Выберите направление",chooseEvent:"Выберите событие",back:"← Назад",book:"Забронировать",yourName:"Ваше имя *",yourPhone:"Телефон *",yourEmail:"Email",notes:"Комментарий",sending:"Отправка...",spotsLeft:t=>`Осталось мест: ${t}`,price:t=>t===0?"Бесплатно":`${t.toLocaleString("ru")} ₽`,errorRequired:"Заполните обязательные поля",errorNetwork:"Ошибка соединения. Попробуйте ещё раз.",noEvents:"Нет доступных событий"};function R(t){return new Date(t).toLocaleDateString("ru-RU",{day:"numeric",month:"long",year:"numeric"})}function s(t,a){const n=document.createElement(t);return a&&(n.className=a),n}function z(){const t=s("div","sw-loading");return t.textContent=p.loading,t}function W(t){const a=s("div","sw-error"),n=s("div");n.textContent="⚠️",n.style.fontSize="2rem",n.style.marginBottom="12px";const e=s("p");e.textContent=t||p.errorNetwork;const r=s("p");return r.style.fontSize="0.875rem",r.style.opacity="0.7",r.style.marginTop="8px",r.textContent="Попробуйте обновить страницу",a.style.textAlign="center",a.style.padding="32px 16px",a.appendChild(n),a.appendChild(e),a.appendChild(r),a}function j(t,a){const n=s("div"),e=s("h2");e.textContent=p.chooseCategory,n.appendChild(e);const r=s("div","sw-grid");if(t.categories.length===0){const d=s("p");return d.textContent=p.noEvents,d.style.opacity="0.6",n.appendChild(d),n}return t.categories.forEach(d=>{const o=s("button","sw-card");if(o.type="button",d.iconUrl){const l=s("img");l.src=d.iconUrl,l.alt="",l.style.width="32px",l.style.height="32px",l.style.marginBottom="8px",l.style.display="block",o.appendChild(l)}const i=s("span");if(i.textContent=d.name,i.style.display="block",i.style.fontWeight="600",o.appendChild(i),d.format){const l=s("span");l.textContent=d.format,l.style.display="block",l.style.fontSize="0.8rem",l.style.marginTop="4px",l.style.opacity="0.7",o.appendChild(l)}o.addEventListener("click",()=>{a({type:"SELECT_CATEGORY",category:d})}),r.appendChild(o)}),n.appendChild(r),n}function _(t,a){var d;const n=s("div");if(((d=t.config)==null?void 0:d.showCategoryStep)&&!t.preselectedCategoryId){const o=s("button","sw-btn-back");o.type="button",o.textContent=p.back,o.addEventListener("click",()=>a({type:"BACK"})),n.appendChild(o)}const r=s("h2");if(r.textContent=p.chooseEvent,t.selectedCategory&&(r.textContent=t.selectedCategory.name),n.appendChild(r),t.events.length===0){const o=s("p");return o.textContent=p.noEvents,o.style.opacity="0.6",n.appendChild(o),n}return t.events.forEach(o=>{const i=s("button","sw-event-card");i.type="button";const l=s("div","sw-event-title");l.textContent=o.title,i.appendChild(l);const f=s("div","sw-event-meta"),c=R(o.startDate),x=R(o.endDate);if(c===x?f.textContent=c:f.textContent=`${c} — ${x}`,i.appendChild(f),o.availableSpots!==void 0&&o.availableSpots!==null){const u=s("div","sw-event-meta");u.textContent=p.spotsLeft(o.availableSpots),u.style.marginTop="4px",i.appendChild(u)}const v=s("div","sw-price");v.textContent=p.price(o.price),i.appendChild(v),i.addEventListener("click",()=>{a({type:"SELECT_EVENT",event:o})}),n.appendChild(i)}),n}function q(t,a){const n=s("div"),e=s("button","sw-btn-back");if(e.type="button",e.textContent=p.back,e.addEventListener("click",()=>a({type:"BACK"})),n.appendChild(e),t.selectedEvent){const g=s("div","sw-selected-event"),b=s("div");b.textContent=t.selectedEvent.title,b.style.fontWeight="600",b.style.marginBottom="4px",g.appendChild(b);const C=s("div"),T=R(t.selectedEvent.startDate),L=R(t.selectedEvent.endDate);T===L?C.textContent=T:C.textContent=`${T} — ${L}`,C.style.opacity="0.7",g.appendChild(C);const $=s("div");$.textContent=p.price(t.selectedEvent.price),$.style.fontWeight="600",$.style.marginTop="4px",g.appendChild($),n.appendChild(g)}const r=s("form");r.noValidate=!0;const d=s("div","sw-form-group"),o=s("label");o.textContent=p.yourName;const i=s("input","sw-input");i.type="text",i.name="name",i.autocomplete="name",i.placeholder="Иван Иванов",d.appendChild(o),d.appendChild(i),r.appendChild(d);const l=s("div","sw-form-group"),f=s("label");f.textContent=p.yourPhone;const c=s("input","sw-input");c.type="tel",c.name="phone",c.autocomplete="tel",c.placeholder="+7 (999) 000-00-00",l.appendChild(f),l.appendChild(c),r.appendChild(l);const x=s("div","sw-form-group"),v=s("label");v.textContent=p.yourEmail;const u=s("input","sw-input");u.type="email",u.name="email",u.autocomplete="email",u.placeholder="example@mail.ru",x.appendChild(v),x.appendChild(u),r.appendChild(x);const B=s("div","sw-form-group"),M=s("label");M.textContent=p.notes;const E=s("textarea","sw-input");E.name="notes",E.rows=3,E.style.resize="vertical",B.appendChild(M),B.appendChild(E),r.appendChild(B);const k=s("div","sw-error");if(k.style.display="none",r.appendChild(k),t.error){const g=s("div","sw-error");g.textContent=t.error,r.appendChild(g)}const S=s("button","sw-btn");return S.type="submit",S.textContent=p.book,r.appendChild(S),r.addEventListener("submit",g=>{g.preventDefault();const b=i.value.trim(),C=c.value.trim(),T=u.value.trim(),L=E.value.trim();if(!b||!C){k.textContent=p.errorRequired,k.style.display="block";return}k.style.display="none",S.disabled=!0,S.textContent=p.sending,a({type:"SUBMIT_FORM",name:b,phone:C,email:T,notes:L})}),n.appendChild(r),n}function K(t){var r,d;const a=s("div","sw-success"),n=s("div","sw-success-icon");n.textContent="✅",a.appendChild(n);const e=s("p");if(e.style.fontWeight="600",e.style.fontSize="1.1rem",e.style.marginBottom="8px",(r=t.booking)!=null&&r.successMessage?e.textContent=t.booking.successMessage:(d=t.config)!=null&&d.successMessage?e.textContent=t.config.successMessage:e.textContent="Бронирование успешно создано!",a.appendChild(e),t.booking){const o=s("p");if(o.textContent=t.booking.eventTitle,o.style.opacity="0.7",o.style.marginBottom="4px",a.appendChild(o),t.booking.amountDue>0){const i=s("p");i.textContent=p.price(t.booking.amountDue),i.style.fontWeight="600",a.appendChild(i)}}return a}function P(t,a){switch(t.step){case"LOADING":return z();case"CATEGORIES":return j(t,a);case"EVENTS":return _(t,a);case"FORM":return q(t,a);case"SUCCESS":return K(t);case"ERROR":return W(t.error);default:return z()}}const I=document.querySelectorAll("script[data-tenant]"),m=I[I.length-1],w=m.dataset.tenant||"",y=m.dataset.apiUrl||"https://api.soldo.ru",N=m.dataset.container||null,h=m.dataset.category?parseInt(m.dataset.category):null;function F(t,a,n){return{step:"LOADING",config:null,categories:[],events:[],selectedCategory:null,selectedEvent:null,booking:null,error:null,apiUrl:t,tenantSlug:a,preselectedCategoryId:n}}async function Y(){var o;let t=null;try{t=await D(y,w)}catch{}if(t){const i=document.createElement("style");i.textContent=V(t),document.head.appendChild(i)}if(N){const i=document.querySelector(N);if(i){i.setAttribute("data-soldo-widget","");const l=F(y,w,h);A(i,l,t);return}}const a=(t==null?void 0:t.buttonLabel)||"Записаться",n=(t==null?void 0:t.primaryColor)||"#2563eb",e=(t==null?void 0:t.buttonTextColor)||"#ffffff",r=(t==null?void 0:t.borderRadius)||"8px",d=document.createElement("button");d.className="sw-trigger-btn",d.textContent=a,d.style.cssText=`
    background:${n};
    color:${e};
    border:none;
    border-radius:${r};
    padding:12px 28px;
    font-size:1rem;
    cursor:pointer;
    transition:opacity 0.15s;
  `,d.onmouseenter=()=>{d.style.opacity="0.85"},d.onmouseleave=()=>{d.style.opacity="1"},(o=m.parentNode)==null||o.insertBefore(d,m),d.addEventListener("click",()=>H(t))}function H(t){const a=document.createElement("div");a.style.cssText=`
    position:fixed;inset:0;z-index:999999;
    background:rgba(0,0,0,0.55);
    display:flex;align-items:center;justify-content:center;
    padding:16px;
    overflow-y:auto;
  `;const n=document.createElement("div");n.style.cssText=`
    position:relative;
    background:#fff;
    border-radius:12px;
    width:100%;
    max-width:560px;
    max-height:90vh;
    overflow-y:auto;
    box-shadow:0 20px 60px rgba(0,0,0,0.3);
    margin:auto;
  `;const e=document.createElement("button");e.textContent="✕",e.style.cssText=`
    position:sticky;top:0;float:right;
    background:transparent;border:none;
    font-size:1.25rem;cursor:pointer;
    color:#6b7280;padding:12px 16px;
    line-height:1;z-index:1;
  `,e.onmouseenter=()=>{e.style.color="#111827"},e.onmouseleave=()=>{e.style.color="#6b7280"};const r=()=>a.remove();e.addEventListener("click",r),a.addEventListener("click",i=>{i.target===a&&r()}),document.addEventListener("keydown",function i(l){l.key==="Escape"&&(r(),document.removeEventListener("keydown",i))});const d=document.createElement("div");d.setAttribute("data-soldo-widget",""),n.appendChild(e),n.appendChild(d),a.appendChild(n),document.body.appendChild(a);const o=F(y,w,h);A(d,o,t)}async function A(t,a,n){let e=a;function r(){t.innerHTML="",t.appendChild(P(e,d))}async function d(o){var i;if(o.type==="SELECT_CATEGORY"){e={...e,step:"EVENTS",selectedCategory:o.category,events:[]},r();try{const l=await O(y,w,o.category.id);e={...e,events:l},r()}catch{e={...e,step:"ERROR",error:"Ошибка загрузки событий"},r()}}else if(o.type==="SELECT_EVENT")e={...e,step:"FORM",selectedEvent:o.event},r();else if(o.type==="BACK"){if(e.step==="FORM")e={...e,step:"EVENTS",selectedEvent:null};else if(e.step==="EVENTS"){if(h||!((i=e.config)!=null&&i.showCategoryStep))return;e={...e,step:"CATEGORIES",selectedCategory:null,events:[]}}r()}else if(o.type==="SUBMIT_FORM"){e={...e,step:"LOADING"},r();try{const l=await U(y,{tenantSlug:w,eventId:e.selectedEvent.id,guestName:o.name,guestPhone:o.phone,guestEmail:o.email||void 0,notes:o.notes||void 0});e={...e,step:"SUCCESS",booking:l},r()}catch{e={...e,step:"FORM",error:"Ошибка при бронировании. Попробуйте ещё раз."},r()}}}if(!n){e={...e,step:"ERROR",error:"Не удалось загрузить виджет"},r();return}if(e={...e,config:n},h||!n.showCategoryStep){e={...e,step:"EVENTS"},r();try{const o=await O(y,w,h||void 0);e={...e,events:o},r()}catch{e={...e,step:"ERROR",error:"Ошибка загрузки событий"},r()}}else try{const o=await G(y,w);e={...e,step:"CATEGORIES",categories:o},r()}catch{e={...e,step:"ERROR",error:"Не удалось загрузить виджет"},r()}}Y()})();
