(function(){"use strict";async function I(t,e){const r=await fetch(`${t}/public/widget/config?tenantSlug=${e}`);if(!r.ok)throw new Error("Failed to load widget config");return r.json()}async function A(t,e){const r=await fetch(`${t}/public/widget/categories?tenantSlug=${e}`);if(!r.ok)throw new Error("Failed to load categories");return r.json()}async function O(t,e,r){let s=`${t}/public/widget/events?tenantSlug=${e}`;r&&(s+=`&categoryId=${r}`);const a=await fetch(s);if(!a.ok)throw new Error("Failed to load events");return a.json()}async function D(t,e){const r=await fetch(`${t}/public/widget/bookings`,{method:"POST",headers:{"Content-Type":"application/json"},body:JSON.stringify(e)});if(!r.ok)throw new Error("Failed to create booking");return r.json()}function M(t){return`
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
  max-width: 560px;
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
`}const p={loading:"Загрузка...",chooseCategory:"Выберите направление",chooseEvent:"Выберите событие",back:"← Назад",book:"Забронировать",yourName:"Ваше имя *",yourPhone:"Телефон *",yourEmail:"Email",notes:"Комментарий",sending:"Отправка...",spotsLeft:t=>`Осталось мест: ${t}`,price:t=>t===0?"Бесплатно":`${t.toLocaleString("ru")} ₽`,errorRequired:"Заполните обязательные поля",errorNetwork:"Ошибка соединения. Попробуйте ещё раз.",noEvents:"Нет доступных событий"};function T(t){return new Date(t).toLocaleDateString("ru-RU",{day:"numeric",month:"long",year:"numeric"})}function o(t,e){const r=document.createElement(t);return e&&(r.className=e),r}function N(){const t=o("div","sw-loading");return t.textContent=p.loading,t}function G(t){const e=o("div","sw-error"),r=o("div");r.textContent="⚠️",r.style.fontSize="2rem",r.style.marginBottom="12px";const s=o("p");s.textContent=t||p.errorNetwork;const a=o("p");return a.style.fontSize="0.875rem",a.style.opacity="0.7",a.style.marginTop="8px",a.textContent="Попробуйте обновить страницу",e.style.textAlign="center",e.style.padding="32px 16px",e.appendChild(r),e.appendChild(s),e.appendChild(a),e}function U(t,e){const r=o("div"),s=o("h2");s.textContent=p.chooseCategory,r.appendChild(s);const a=o("div","sw-grid");if(t.categories.length===0){const l=o("p");return l.textContent=p.noEvents,l.style.opacity="0.6",r.appendChild(l),r}return t.categories.forEach(l=>{const n=o("button","sw-card");if(n.type="button",l.iconUrl){const d=o("img");d.src=l.iconUrl,d.alt="",d.style.width="32px",d.style.height="32px",d.style.marginBottom="8px",d.style.display="block",n.appendChild(d)}const i=o("span");if(i.textContent=l.name,i.style.display="block",i.style.fontWeight="600",n.appendChild(i),l.format){const d=o("span");d.textContent=l.format,d.style.display="block",d.style.fontSize="0.8rem",d.style.marginTop="4px",d.style.opacity="0.7",n.appendChild(d)}n.addEventListener("click",()=>{e({type:"SELECT_CATEGORY",category:l})}),a.appendChild(n)}),r.appendChild(a),r}function V(t,e){var l;const r=o("div");if(((l=t.config)==null?void 0:l.showCategoryStep)&&!t.preselectedCategoryId){const n=o("button","sw-btn-back");n.type="button",n.textContent=p.back,n.addEventListener("click",()=>e({type:"BACK"})),r.appendChild(n)}const a=o("h2");if(a.textContent=p.chooseEvent,t.selectedCategory&&(a.textContent=t.selectedCategory.name),r.appendChild(a),t.events.length===0){const n=o("p");return n.textContent=p.noEvents,n.style.opacity="0.6",r.appendChild(n),r}return t.events.forEach(n=>{const i=o("button","sw-event-card");i.type="button";const d=o("div","sw-event-title");d.textContent=n.title,i.appendChild(d);const f=o("div","sw-event-meta"),c=T(n.startDate),h=T(n.endDate);if(c===h?f.textContent=c:f.textContent=`${c} — ${h}`,i.appendChild(f),n.availableSpots!==void 0&&n.availableSpots!==null){const u=o("div","sw-event-meta");u.textContent=p.spotsLeft(n.availableSpots),u.style.marginTop="4px",i.appendChild(u)}const x=o("div","sw-price");x.textContent=p.price(n.price),i.appendChild(x),i.addEventListener("click",()=>{e({type:"SELECT_EVENT",event:n})}),r.appendChild(i)}),r}function W(t,e){const r=o("div"),s=o("button","sw-btn-back");if(s.type="button",s.textContent=p.back,s.addEventListener("click",()=>e({type:"BACK"})),r.appendChild(s),t.selectedEvent){const g=o("div","sw-selected-event"),m=o("div");m.textContent=t.selectedEvent.title,m.style.fontWeight="600",m.style.marginBottom="4px",g.appendChild(m);const y=o("div"),S=T(t.selectedEvent.startDate),L=T(t.selectedEvent.endDate);S===L?y.textContent=S:y.textContent=`${S} — ${L}`,y.style.opacity="0.7",g.appendChild(y);const $=o("div");$.textContent=p.price(t.selectedEvent.price),$.style.fontWeight="600",$.style.marginTop="4px",g.appendChild($),r.appendChild(g)}const a=o("form");a.noValidate=!0;const l=o("div","sw-form-group"),n=o("label");n.textContent=p.yourName;const i=o("input","sw-input");i.type="text",i.name="name",i.autocomplete="name",i.placeholder="Иван Иванов",l.appendChild(n),l.appendChild(i),a.appendChild(l);const d=o("div","sw-form-group"),f=o("label");f.textContent=p.yourPhone;const c=o("input","sw-input");c.type="tel",c.name="phone",c.autocomplete="tel",c.placeholder="+7 (999) 000-00-00",d.appendChild(f),d.appendChild(c),a.appendChild(d);const h=o("div","sw-form-group"),x=o("label");x.textContent=p.yourEmail;const u=o("input","sw-input");u.type="email",u.name="email",u.autocomplete="email",u.placeholder="example@mail.ru",h.appendChild(x),h.appendChild(u),a.appendChild(h);const B=o("div","sw-form-group"),F=o("label");F.textContent=p.notes;const v=o("textarea","sw-input");v.name="notes",v.rows=3,v.style.resize="vertical",B.appendChild(F),B.appendChild(v),a.appendChild(B);const E=o("div","sw-error");if(E.style.display="none",a.appendChild(E),t.error){const g=o("div","sw-error");g.textContent=t.error,a.appendChild(g)}const k=o("button","sw-btn");return k.type="submit",k.textContent=p.book,a.appendChild(k),a.addEventListener("submit",g=>{g.preventDefault();const m=i.value.trim(),y=c.value.trim(),S=u.value.trim(),L=v.value.trim();if(!m||!y){E.textContent=p.errorRequired,E.style.display="block";return}E.style.display="none",k.disabled=!0,k.textContent=p.sending,e({type:"SUBMIT_FORM",name:m,phone:y,email:S,notes:L})}),r.appendChild(a),r}function _(t){var a,l;const e=o("div","sw-success"),r=o("div","sw-success-icon");r.textContent="✅",e.appendChild(r);const s=o("p");if(s.style.fontWeight="600",s.style.fontSize="1.1rem",s.style.marginBottom="8px",(a=t.booking)!=null&&a.successMessage?s.textContent=t.booking.successMessage:(l=t.config)!=null&&l.successMessage?s.textContent=t.config.successMessage:s.textContent="Бронирование успешно создано!",e.appendChild(s),t.booking){const n=o("p");if(n.textContent=t.booking.eventTitle,n.style.opacity="0.7",n.style.marginBottom="4px",e.appendChild(n),t.booking.amountDue>0){const i=o("p");i.textContent=p.price(t.booking.amountDue),i.style.fontWeight="600",e.appendChild(i)}}return e}function j(t,e){switch(t.step){case"LOADING":return N();case"CATEGORIES":return U(t,e);case"EVENTS":return V(t,e);case"FORM":return W(t,e);case"SUCCESS":return _(t);case"ERROR":return G(t.error);default:return N()}}const z=document.querySelectorAll("script[data-tenant]"),w=z[z.length-1],C=w.dataset.tenant||"",b=w.dataset.apiUrl||"https://api.soldo.ru",q=w.dataset.container||"#soldo-widget",R=w.dataset.category?parseInt(w.dataset.category):null;async function P(){var l;let t=document.querySelector(q);t||(t=document.createElement("div"),t.id="soldo-widget",(l=w.parentNode)==null||l.insertBefore(t,w)),t.setAttribute("data-soldo-widget","");let e={step:"LOADING",config:null,categories:[],events:[],selectedCategory:null,selectedEvent:null,booking:null,error:null,apiUrl:b,tenantSlug:C,preselectedCategoryId:R};function r(n){a(n)}function s(){t.innerHTML="",t.appendChild(j(e,r))}async function a(n){var i;if(n.type==="SELECT_CATEGORY"){e={...e,step:"EVENTS",selectedCategory:n.category,events:[]},s();try{const d=await O(b,C,n.category.id);e={...e,events:d},s()}catch{e={...e,step:"ERROR",error:"Ошибка загрузки событий"},s()}}else if(n.type==="SELECT_EVENT")e={...e,step:"FORM",selectedEvent:n.event},s();else if(n.type==="BACK"){if(e.step==="FORM")e={...e,step:"EVENTS",selectedEvent:null};else if(e.step==="EVENTS"){if(R||!((i=e.config)!=null&&i.showCategoryStep))return;e={...e,step:"CATEGORIES",selectedCategory:null,events:[]}}s()}else if(n.type==="SUBMIT_FORM"){e={...e,step:"LOADING"},s();try{const d=await D(b,{tenantSlug:C,eventId:e.selectedEvent.id,guestName:n.name,guestPhone:n.phone,guestEmail:n.email||void 0,notes:n.notes||void 0});e={...e,step:"SUCCESS",booking:d},s()}catch{e={...e,step:"FORM",error:"Ошибка при бронировании. Попробуйте ещё раз."},s()}}}s();try{const n=await I(b,C),i=document.createElement("style");if(i.textContent=M(n),document.head.appendChild(i),e={...e,config:n},R||!n.showCategoryStep){e={...e,step:"EVENTS"},s();const d=await O(b,C,R||void 0);e={...e,events:d},s()}else{const d=await A(b,C);e={...e,step:"CATEGORIES",categories:d},s()}}catch{e={...e,step:"ERROR",error:"Не удалось загрузить виджет"},s()}}P()})();
