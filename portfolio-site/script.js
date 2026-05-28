const routes = {
  "/": "home-template",
  "/resume": "resume-template",
  "/pharmacy-system": "pharmacy-template",
  "/music-player": "music-template",
};

const app = document.querySelector("#app");
const navLinks = [...document.querySelectorAll(".main-nav a")];

function currentPath() {
  const hash = window.location.hash.replace(/^#/, "");
  if (routes[hash]) return hash;

  const path = window.location.pathname.replace(/\/index\.html$/, "") || "/";
  return routes[path] ? path : "/";
}

function render() {
  const path = currentPath();
  const template = document.querySelector(`#${routes[path]}`);

  app.innerHTML = "";
  app.append(template.content.cloneNode(true));

  navLinks.forEach((link) => {
    link.classList.toggle("active", link.getAttribute("href") === `#${path}`);
  });

  window.scrollTo(0, 0);
  document.title = pageTitle(path);
}

function pageTitle(path) {
  const titles = {
    "/": "姚学良（姚六木） | Java 后端开发作品集",
    "/resume": "姚学良 | 网页版简历",
    "/pharmacy-system": "乡镇药店管理系统 | 姚学良作品集",
    "/music-player": "NewPlayer 本地音乐播放器 | 姚学良作品集",
  };
  return titles[path] || titles["/"];
}

window.addEventListener("hashchange", render);
render();
