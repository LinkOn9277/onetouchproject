$(function () {



    $(".imgInput").on("change", function () {
        insertImg();
    })
    $(".imgDelete").on("click",function () {
        deleteImg();
    })

    function insertImg() {

        let currentUrl = window.location.pathname;
        let imgType ="square";

        if(currentUrl.includes("/members/hotel")){
            imgType = "banner";
        }

        let formData = new FormData();
        formData.append("image", $(".imgInput")[0].files[0])
        formData.append("imgType", imgType)
        $.ajax({

            url: "/img/register",
            processData: false,
            type: "post",
            enctype: "multipart/form-data",
            data: formData,
            cache: false,
            contentType: false,
            success: function (result) {

                $.each(result, function(key,value){
                    var imgNums = key;
                    $(".hiddenImgNum").html('<input type="hidden" name="imgNum" value="' + key + '"/>')
                    var imgFullUrl = value;
                    if($(".menuImg").attr("th:src")){
                        $(".menuImg").removeAttr("th:src")
                    }
                    $(".menuImg").attr("src",imgFullUrl);
                })

            },
            error: function () {
                alert("에러 발생")
            }

        })

    }

    function showImg(){

        let imgNum = 18

        $.ajax({
            url:"/img/showImg/"+imgNum,
            type:"get",
            success:function (result) {
                let str = result;


            },
            error:function () {

            }

        })

    }

    function deleteImg(){

        let imgNum = $(this).data("imgnum")


        $.ajax({

            url:"/img/delete/"+imgNum,
            type: "delete",
            success(result){
                alert(result)
            },
            error(){
                alert("삭제 에러 발생")
            }

        })

    }

    function updateImg(){
        let imgNum = $(this).data("imgnum");
        let formData = new FormData();
        formData.append("image", $(".imgInput")[0].files[0])
        formData.append("imgNum", imgNum)

        $.ajax({

            url:"/img/update",
            type:"post",
            enctype: "multipart/form-data",
            data: formData,
            cache: false,
            contentType: false,
            success: function (imgNum) {
                alert(imgNum+"수정 성공")

            },
            error: function () {
                alert("에러 발생")
            }
        })
    }


})


