import { useContext, useEffect, useState } from "react";
import { Link, Outlet, useNavigate, useParams } from "react-router-dom";
import "./Rent_admin.css";
import axios from "axios";
import Rent_item_return from "./Rent_item_return";
import bookLogo from "../../assets/MainLogo.png";
import AdmLayout from "../AdmLayout"

const Rent_admin_return = () => {
  const [posts, setPosts] = useState([]);

  useEffect(() => {
    const instance = axios.create({
      baseURL: "http://localhost:8080/rents",
      timeout: 10000,
    });
    instance
      .get("/approval/2")
      // rent뒤의 1은 도서상태 1은 미승인, 2는 승인, 3은 반려처리 4 반납완료?, 5 사고처리?
      // 현재페이지는 반납완료처리 페이지
      .then((data) => {
        setPosts(data.data);
      })
      .catch((error) => console.error("Error fetching posts:", error));
  }, []);

  console.log(posts)

  return (
    <div className="Rent_Admin_List">
     <AdmLayout/>

      <div className="rent_admin_wrap adm_con">
        <div className="rent_admin_header">
          <h3 className="rent_admin_tt">대여 현황 및 반납신청</h3>
        </div>
        <div className="rent_admin_body">
          <ul className="rent_item">
            {posts && posts.map((it) => <Rent_item_return key={it.id} {...it} />)}
          </ul>
        </div>
        <div className="rent_admin_tail">
          <div className="btn_list"></div>
        </div>
      </div>
    </div>
  );
};

export default Rent_admin_return;
