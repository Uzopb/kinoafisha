/* ============================================================
   КиноАфиша — прототип на реальных данных TMDB
   Экраны: Новинки (лента + поиск) · Каталог (жанры/направления)
           Избранное · Детали (описание + отзывы, read-only)
   ============================================================ */

"use strict";

/* ---------- TMDB API ---------- */

const API_KEY = "d3c18a34918f783dfc20a19c00c3c4c3";
const API = "https://api.themoviedb.org/3";
const IMG_CARD = "https://image.tmdb.org/t/p/w342";
const IMG_DETAILS = "https://image.tmdb.org/t/p/w500";
const LANG = "ru-RU";

async function tmdb(path, params = {}) {
  const qs = new URLSearchParams({ api_key: API_KEY, language: LANG, ...params });
  const res = await fetch(`${API}${path}?${qs}`);
  if (!res.ok) throw new Error(`TMDB ${res.status}: ${path}`);
  return res.json();
}

/* ---------- Состояние ---------- */

const state = {
  view: "feed",
  prevView: "feed",
  query: "",
  genre: null,          // id жанра из бокового меню
  detailsId: null,      // "movie:123" | "tv:456"
  sort: "popularity",   // popularity | rating.desc|asc | year.desc|asc
  favSort: "popularity",
  page: 1,
  totalPages: 1,
  feedItems: [],
  feedLoading: false,
  layout: "list",       // list | grid
};

const genreMaps = { movie: new Map(), tv: new Map() };      // id -> name
const cache = new Map();                                     // "movie:123" -> item

const FAV_KEY = "kinoafisha:favorites:v2";
const LAYOUT_KEY = "kinoafisha:layout";
const favorites = new Map(JSON.parse(localStorage.getItem(FAV_KEY) || "[]")); // key -> snapshot
const savedLayout = localStorage.getItem(LAYOUT_KEY);
if (savedLayout === "list" || savedLayout === "grid") state.layout = savedLayout;

const $ = (sel) => document.querySelector(sel);
const $$ = (sel) => [...document.querySelectorAll(sel)];

/* ---------- Нормализация ---------- */

function normalize(raw, media) {
  const date = raw.release_date || raw.first_air_date || "";
  const gmap = genreMaps[media] || genreMaps.movie;
  const item = {
    key: `${media}:${raw.id}`,
    id: raw.id,
    media,
    title: raw.title || raw.name || "Без названия",
    year: date ? date.slice(0, 4) : "—",
    releaseDate: date,
    genres: (raw.genre_ids || (raw.genres || []).map((g) => g.id)).map((id) => gmap.get(id)).filter(Boolean),
    rating: raw.vote_average || 0,
    votes: raw.vote_count || 0,
    poster: raw.poster_path ? `${IMG_CARD}${raw.poster_path}` : null,
    posterBig: raw.poster_path ? `${IMG_DETAILS}${raw.poster_path}` : null,
    desc: raw.overview || "Описание пока отсутствует.",
    isNew: date ? Date.now() - new Date(date) < 21 * 864e5 : false,
  };
  cache.set(item.key, item);
  return item;
}

/* ---------- Утилиты ---------- */

function ratingClass(r) {
  return r >= 7.5 ? "rating-pill--high" : r >= 6 ? "rating-pill--mid" : "rating-pill--low";
}

function saveFavorites() {
  localStorage.setItem(FAV_KEY, JSON.stringify([...favorites]));
}

function plural(n, one, few, many) {
  const m10 = n % 10, m100 = n % 100;
  if (m10 === 1 && m100 !== 11) return one;
  if (m10 >= 2 && m10 <= 4 && (m100 < 12 || m100 > 14)) return few;
  return many;
}

function esc(s) {
  return String(s).replace(/[&<>"']/g, (c) =>
    ({ "&": "&amp;", "<": "&lt;", ">": "&gt;", '"': "&quot;", "'": "&#39;" }[c]));
}

/** То же сердце, что в bottom bar */
function heartIcon(size = 18) {
  return `<svg class="fav-btn__icon" viewBox="0 0 24 24" width="${size}" height="${size}" aria-hidden="true"><path d="M12 21s-8-4.7-10.5-9.5C-.3 7.9 2.3 4 6.2 4c2.3 0 3.9 1.3 5.8 3.4C13.9 5.3 15.5 4 17.8 4c3.9 0 6.5 3.9 4.7 7.5C20 16.3 12 21 12 21z"/></svg>`;
}

function setFavState(btn, isFav) {
  btn.classList.toggle("is-fav", isFav);
  if (!btn.querySelector(".fav-btn__icon")) {
    btn.innerHTML = heartIcon(btn.classList.contains("fav-btn--lg") ? 22 : 18);
  }
}

/** Клиентская сортировка списка карточек */
function sortMovies(list, sort) {
  const arr = [...list];
  switch (sort) {
    case "rating.desc":
      return arr.sort((a, b) => b.rating - a.rating || b.votes - a.votes);
    case "rating.asc":
      return arr.sort((a, b) => a.rating - b.rating || a.votes - b.votes);
    case "year.desc":
      return arr.sort((a, b) => (b.releaseDate || "").localeCompare(a.releaseDate || ""));
    case "year.asc":
      return arr.sort((a, b) => (a.releaseDate || "").localeCompare(b.releaseDate || ""));
    default:
      return arr;
  }
}

/** sort_by для TMDB /discover */
function tmdbSortBy(sort) {
  switch (sort) {
    case "rating.desc": return "vote_average.desc";
    case "rating.asc": return "vote_average.asc";
    case "year.desc": return "primary_release_date.desc";
    case "year.asc": return "primary_release_date.asc";
    default: return "popularity.desc";
  }
}

function skeletons(box, n = 5) {
  box.innerHTML = "";
  for (let i = 0; i < n; i++) {
    const el = document.createElement("div");
    el.className = "movie-card movie-card--skeleton";
    el.innerHTML = `<div class="movie-card__poster"></div>
      <div class="movie-card__body">
        <div class="sk sk--title"></div><div class="sk sk--line"></div><div class="sk sk--line sk--short"></div>
      </div>`;
    box.appendChild(el);
  }
}

function showError(box, emptyEl, retry) {
  box.innerHTML = "";
  emptyEl.hidden = false;
  emptyEl.innerHTML = `Не удалось загрузить данные. <button class="link" type="button">Повторить</button>`;
  emptyEl.querySelector(".link").addEventListener("click", retry);
}

/* ---------- Рендер: карточка ---------- */

function movieCard(m) {
  const isFav = favorites.has(m.key);
  const card = document.createElement("button");
  card.type = "button";
  card.className = "movie-card";
  card.dataset.key = m.key;
  card.innerHTML = `
    <div class="movie-card__poster">
      ${m.poster
        ? `<img src="${m.poster}" alt="${esc(m.title)}" loading="lazy" />`
        : `<span class="movie-card__no-poster">🎞️</span>`}
      ${m.isNew ? '<span class="movie-card__badge-new">new</span>' : ""}
    </div>
    <div class="movie-card__body">
      <div class="movie-card__title">${esc(m.title)}</div>
      <div class="movie-card__meta">${m.year}${m.genres.length ? " · " + esc(m.genres.slice(0, 3).join(", ")) : ""}</div>
      <div class="movie-card__desc">${esc(m.desc)}</div>
      <div class="movie-card__foot">
        <span class="rating-pill ${ratingClass(m.rating)}">★ ${m.rating.toFixed(1)} <small>· ${m.votes.toLocaleString("ru-RU")}</small></span>
        <button class="fav-btn ${isFav ? "is-fav" : ""}" type="button" aria-label="В избранное">${heartIcon(18)}</button>
      </div>
    </div>`;

  card.addEventListener("click", () => openDetails(m.key));
  card.querySelector(".fav-btn").addEventListener("click", (e) => {
    e.stopPropagation();
    toggleFavorite(m);
  });
  return card;
}

/* ---------- Экран: Новинки ---------- */

function releaseWindow() {
  const to = new Date().toISOString().slice(0, 10);
  const from = new Date();
  from.setMonth(from.getMonth() - 2);
  return { from: from.toISOString().slice(0, 10), to };
}

async function fetchFeedPage(page) {
  const q = state.query.trim();
  if (q) {
    return tmdb("/search/movie", { query: q, include_adult: "false", page: String(page) });
  }
  if (state.genre != null) {
    const params = {
      with_genres: String(state.genre),
      sort_by: tmdbSortBy(state.sort),
      include_adult: "false",
      page: String(page),
    };
    if (state.sort.startsWith("rating.")) params["vote_count.gte"] = "50";
    return tmdb("/discover/movie", params);
  }
  if (state.sort !== "popularity") {
    const { from, to } = releaseWindow();
    const params = {
      sort_by: tmdbSortBy(state.sort),
      include_adult: "false",
      region: "RU",
      "primary_release_date.lte": to,
      "primary_release_date.gte": from,
      page: String(page),
    };
    if (state.sort.startsWith("rating.")) params["vote_count.gte"] = "50";
    return tmdb("/discover/movie", params);
  }
  return tmdb("/movie/now_playing", { region: "RU", page: String(page) });
}

function paintFeed(list) {
  const box = $("#feedList");
  box.innerHTML = "";
  list.forEach((m) => box.appendChild(movieCard(m)));
}

function updateMoreButton() {
  const more = $("#feedMore");
  const hasMore = state.page < state.totalPages && state.feedItems.length > 0;
  more.hidden = !hasMore;
  more.disabled = state.feedLoading;
  more.textContent = state.feedLoading ? "Загрузка…" : "Дальше";
}

/** @param {{ append?: boolean }} [opts] */
async function renderFeed({ append = false } = {}) {
  if (state.feedLoading) return;

  const box = $("#feedList");
  const empty = $("#feedEmpty");
  const more = $("#feedMore");
  const q = state.query.trim();
  const sortSnap = state.sort;
  const genreSnap = state.genre;
  const page = append ? state.page + 1 : 1;

  state.feedLoading = true;

  if (!append) {
    empty.hidden = true;
    more.hidden = true;
    state.feedItems = [];
    state.page = 1;
    state.totalPages = 1;
    skeletons(box);
  } else {
    updateMoreButton();
  }

  let failed = false;
  try {
    const data = await fetchFeedPage(page);

    // запрос мог устареть, пока летел
    if (
      state.query.trim() !== q ||
      state.sort !== sortSnap ||
      state.genre !== genreSnap
    ) {
      return;
    }

    const pageItems = (data.results || []).map((r) => normalize(r, "movie"));
    // убираем дубликаты при склейке страниц
    const seen = new Set(append ? state.feedItems.map((m) => m.key) : []);
    const unique = pageItems.filter((m) => !seen.has(m.key));
    state.feedItems = append ? state.feedItems.concat(unique) : unique;
    state.page = data.page || page;
    state.totalPages = data.total_pages || 1;

    let list = state.feedItems;
    // поиск и now_playing / discover без жанра — клиентская сортировка всего накопленного
    if (q || state.genre == null) list = sortMovies(list, state.sort);

    paintFeed(list);
    empty.hidden = list.length > 0;
    if (!list.length) empty.textContent = "По запросу ничего не найдено.";
  } catch {
    failed = true;
    if (append) {
      more.hidden = false;
      more.disabled = false;
      more.textContent = "Не удалось · ещё раз";
    } else {
      more.hidden = true;
      showError(box, empty, () => renderFeed());
    }
  } finally {
    state.feedLoading = false;
    const stillCurrent =
      state.query.trim() === q && state.sort === sortSnap && state.genre === genreSnap;
    if (stillCurrent && !failed) updateMoreButton();
  }
}

/* ---------- Жанры + боковое меню ---------- */

async function loadGenres() {
  const [mg, tg] = await Promise.all([
    tmdb("/genre/movie/list"),
    tmdb("/genre/tv/list"),
  ]);
  mg.genres.forEach((g) => genreMaps.movie.set(g.id, g.name));
  tg.genres.forEach((g) => genreMaps.tv.set(g.id, g.name));
}

function renderGenreList() {
  const box = $("#genreList");
  box.innerHTML = "";
  for (const [id, name] of genreMaps.movie) {
    const el = document.createElement("button");
    el.type = "button";
    el.className = "drawer__genre" + (state.genre === id ? " is-active" : "");
    el.textContent = name;
    el.addEventListener("click", () => selectGenre(id));
    box.appendChild(el);
  }
}

function selectGenre(id) {
  state.genre = state.genre === id ? null : id;
  // жанр и поиск взаимоисключающи
  state.query = "";
  searchInput.value = "";
  searchClear.hidden = true;
  closeDrawer();
  renderGenreList();
  updateFeedTitle();
  if (state.view !== "feed") switchView("feed");
  renderFeed();
}

function updateFeedTitle() {
  const name = state.genre != null ? genreMaps.movie.get(state.genre) : null;
  $("#feedTitle").textContent = name
    ? name[0].toUpperCase() + name.slice(1)
    : "Новинки";
}

function openDrawer() {
  $("#drawer").hidden = false;
  document.body.classList.add("no-scroll");
}

function closeDrawer() {
  $("#drawer").hidden = true;
  document.body.classList.remove("no-scroll");
}

$("#burger").addEventListener("click", openDrawer);
$$("[data-drawer-close]").forEach((el) => el.addEventListener("click", closeDrawer));
document.addEventListener("keydown", (e) => {
  if (e.key === "Escape") closeDrawer();
});

/* ---------- Экран: Избранное ---------- */

function renderFavorites() {
  const box = $("#favList");
  box.innerHTML = "";
  const list = sortMovies([...favorites.values()], state.favSort);
  list.forEach((m) => box.appendChild(movieCard(m)));
  $("#favEmpty").hidden = favorites.size > 0;
}

/* ---------- Экран: Детали ---------- */

function openDetails(key) {
  state.detailsId = key;
  state.prevView = state.view === "details" ? state.prevView : state.view;
  switchView("details");
  renderDetails();
  window.scrollTo({ top: 0 });
}

function pickCertification(d, media) {
  if (media === "movie") {
    const list = d.release_dates?.results || [];
    const pref = list.find((c) => c.iso_3166_1 === "RU") || list.find((c) => c.iso_3166_1 === "US");
    return pref?.release_dates?.find((r) => r.certification)?.certification || null;
  }
  const list = d.content_ratings?.results || [];
  return (list.find((c) => c.iso_3166_1 === "RU") || list.find((c) => c.iso_3166_1 === "US"))?.rating || null;
}

async function renderDetails() {
  const key = state.detailsId;
  const base = cache.get(key) || favorites.get(key);
  if (!base) return;

  // сразу рисуем то, что есть из списка
  fillDetails(base, null, null);

  try {
    const [media, id] = key.split(":");
    const extra = media === "movie" ? "reviews,release_dates" : "reviews,content_ratings";
    const d = await tmdb(`/${media}/${id}`, { append_to_response: extra });
    if (state.detailsId !== key) return; // пользователь уже ушёл

    const full = { ...base, desc: d.overview || base.desc };
    fillDetails(full, pickCertification(d, media), d.reviews?.results || []);
  } catch {
    $("#reviewsList").innerHTML = `<p class="empty">Не удалось загрузить отзывы.</p>`;
  }
}

function fillDetails(m, age, reviews) {
  $("#dPoster").style.background = "none";
  $("#dPoster").innerHTML = m.posterBig
    ? `<img src="${m.posterBig}" alt="${esc(m.title)}" />`
    : `<span class="details__emoji">🎞️</span>`;
  $("#dTitle").textContent = m.title;
  $("#dMeta").textContent =
    `${m.media === "tv" ? "Сериал" : "Фильм"} · ${m.year}` +
    (m.genres.length ? ` · ${m.genres.join(", ")}` : "") +
    (age ? ` · ${age}` : "");
  $("#dRating").innerHTML =
    `<span class="rating-pill ${ratingClass(m.rating)}">★ ${m.rating.toFixed(1)} <small>· ${m.votes.toLocaleString("ru-RU")} оценок</small></span>`;
  $("#dDesc").textContent = m.desc;

  setFavState($("#dFav"), favorites.has(m.key));

  if (reviews === null) {
    $("#reviewsCount").textContent = "";
    $("#reviewsList").innerHTML = `<p class="empty">Загрузка отзывов…</p>`;
    return;
  }

  $("#reviewsCount").textContent = reviews.length
    ? `· ${reviews.length} ${plural(reviews.length, "отзыв", "отзыва", "отзывов")}`
    : "";

  const box = $("#reviewsList");
  box.innerHTML = "";
  if (!reviews.length) {
    box.innerHTML = `<p class="empty">Отзывов пока нет.</p>`;
    return;
  }
  for (const r of reviews) {
    const name = r.author_details?.name || r.author || "Аноним";
    const rating = r.author_details?.rating; // 0..10 или null
    const stars = rating ? Math.round(rating / 2) : 0;
    const date = (r.created_at || "").slice(0, 10).split("-").reverse().join(".");
    const hue = [...name].reduce((s, c) => s + c.charCodeAt(0), 0) % 360;
    const el = document.createElement("article");
    el.className = "review";
    el.innerHTML = `
      <div class="review__head">
        <span class="review__avatar" style="background: hsl(${hue} 55% 45%)">${esc(name[0] || "?").toUpperCase()}</span>
        <div>
          <div class="review__author">${esc(name)}</div>
          <div class="review__date">${date}</div>
        </div>
        ${rating ? `<span class="review__stars">${"★".repeat(stars)}${"☆".repeat(5 - stars)}</span>` : ""}
      </div>
      <p class="review__text">${esc(r.content || "")}</p>`;
    box.appendChild(el);
  }
}

/* ---------- Избранное ---------- */

function toggleFavorite(m) {
  favorites.has(m.key) ? favorites.delete(m.key) : favorites.set(m.key, m);
  saveFavorites();
  const isFav = favorites.has(m.key);
  $$(`.movie-card[data-key="${m.key}"] .fav-btn`).forEach((btn) => setFavState(btn, isFav));
  if (state.view === "favorites") renderFavorites();
  if (state.view === "details" && state.detailsId === m.key) {
    setFavState($("#dFav"), isFav);
  }
}

/* ---------- Навигация ---------- */

function switchView(name) {
  state.view = name;
  $$(".view").forEach((v) => v.classList.toggle("is-active", v.id === `view-${name}`));
  $$("[data-nav]").forEach((el) => el.classList.toggle("is-active", el.dataset.nav === name));
  $("#searchWrap").style.visibility = name === "details" ? "hidden" : "visible";
}

$$("[data-nav]").forEach((el) =>
  el.addEventListener("click", (e) => {
    e.preventDefault();
    const name = el.dataset.nav;
    if (name === "feed" && (state.genre != null || state.query)) {
      state.genre = null;
      state.query = "";
      searchInput.value = "";
      searchClear.hidden = true;
      renderGenreList();
      updateFeedTitle();
      switchView("feed");
      renderFeed();
      return;
    }
    switchView(name);
  })
);

$("#detailsBack").addEventListener("click", () => switchView(state.prevView));

/* ---------- Поиск (с debounce) ---------- */

const searchInput = $("#searchInput");
const searchClear = $("#searchClear");
let searchTimer;

searchInput.addEventListener("input", () => {
  state.query = searchInput.value;
  searchClear.hidden = !state.query;
  // поиск сбрасывает жанровый фильтр
  if (state.genre != null) {
    state.genre = null;
    renderGenreList();
    updateFeedTitle();
  }
  clearTimeout(searchTimer);
  searchTimer = setTimeout(() => {
    if (state.view !== "feed") switchView("feed");
    renderFeed();
  }, 400);
});

searchClear.addEventListener("click", () => {
  searchInput.value = "";
  state.query = "";
  searchClear.hidden = true;
  renderFeed();
  searchInput.focus();
});

/* ---------- Сортировка ---------- */

$("#sortSelect").addEventListener("change", () => {
  state.sort = $("#sortSelect").value;
  renderFeed();
});

$("#favSortSelect").addEventListener("change", () => {
  state.favSort = $("#favSortSelect").value;
  renderFavorites();
});

$("#feedMore").addEventListener("click", () => renderFeed({ append: true }));

/* ---------- Вид: список / сетка ---------- */

function applyLayout() {
  const isGrid = state.layout === "grid";
  $("#feedList").classList.toggle("feed--grid", isGrid);
  $("#favList").classList.toggle("feed--grid", isGrid);
  $$(".layout-toggle__btn").forEach((btn) => {
    btn.classList.toggle("is-active", btn.dataset.layout === state.layout);
  });
}

$$(".layout-toggle__btn").forEach((btn) => {
  btn.addEventListener("click", () => {
    const next = btn.dataset.layout;
    if (next !== "list" && next !== "grid") return;
    state.layout = next;
    localStorage.setItem(LAYOUT_KEY, next);
    applyLayout();
  });
});

$("#dFav").addEventListener("click", () => {
  const m = cache.get(state.detailsId) || favorites.get(state.detailsId);
  if (m) toggleFavorite(m);
});

/* ---------- Инициализация ---------- */

(async function init() {
  applyLayout();
  renderFavorites();
  try {
    await loadGenres();
  } catch { /* жанры подтянутся пустыми, карточки всё равно отрисуются */ }
  renderGenreList();
  updateFeedTitle();
  renderFeed();
})();
