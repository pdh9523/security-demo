"use client"

import {api} from "@/lib/api";
import {useEffect, useState} from "react";


export default function App() {
    const [email, setEmail] = useState("");

    function logout() {
        api.post("/user/logout")
            .then(response => console.log(response))
    }

    function myInfo() {
        api.get("/user/my-info")
            .then(response => {
                    console.log(response.data)
                    setEmail(response.data.email)
                }
            )
    }

    useEffect(() => {
        myInfo()
    })
    return (
        <div>
            <div>
                {email}
            </div>
            <div>
                랜딩 페이지 입니다.
                <button
                    onClick={() => logout()}
                >
                    로그아웃
                </button>
            </div>
        </div>
    )
}