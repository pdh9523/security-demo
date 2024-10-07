"use client";

import {useRouter, useSearchParams} from "next/navigation";
import {useEffect} from "react";
import {signIn} from "next-auth/react";

export default function App() {
    const router = useRouter();
    const searchParams = useSearchParams();

    useEffect(() => {
        const accessToken = searchParams.get("accessToken")
        const refreshToken = searchParams.get("refreshToken")
        if(accessToken && refreshToken) {
            signIn("kakao", {
                redirect: false,
                accessToken,
                refreshToken,
            })
                .then(() => router.push("/"));
        }
    }, [router])

    return <div>Redirecting...</div>;
}