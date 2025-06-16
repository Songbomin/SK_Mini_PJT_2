// src/pages/ItemDetail.js
import React, { useState, useEffect } from 'react';
import { Container, Card, Button, Row, Col } from 'react-bootstrap';
import { useParams, useNavigate, Link } from 'react-router-dom';
import { FaTrash, FaThumbsUp, FaHeart } from 'react-icons/fa';
import { useAuth } from '../context/AuthContext';
import { postsAPI } from '../api/posts';
import { getWishlistItems, toggleWish, toggleWishdel } from '../api/wishlistApi';
//import { items } from '../data/dummyData'; // 더미 데이터 import
import Pagination from '../components/Pagination';
import '../styles/common.css';
import '../styles/ItemDetail.css';
import axios from "axios";
import { hasDeletePermission } from '../utils/authUtils';

function ItemDetail() {
  const { id } = useParams();
  const navigate = useNavigate();
  const { user } = useAuth();
  const [item, setItem] = useState(null);
  const [commentList, setCommentList] = useState([]);
  const [newComment, setNewComment] = useState('');
  const [currentPage, setCurrentPage] = useState(1);
  const [itemsPerPage] = useState(10);
  const [isLiked, setIsLiked] = useState(false);
  const [likeCount, setLikeCount] = useState(0);
  const [editingCommentId, setEditingCommentId] = useState(null);
  const [editCommentContent, setEditCommentContent] = useState('');
  const [currentCommentPage, setCurrentCommentPage] = useState(1);
  const [commentsPerPage] = useState(5);
  const [isFollowing, setIsFollowing] = useState(false);
  const [isBackNavigation, setIsBackNavigation] = useState(false);

  const indexOfLastComment = currentCommentPage * commentsPerPage;
  const indexOfFirstComment = indexOfLastComment - commentsPerPage;
  const currentComments = commentList.slice(indexOfFirstComment, indexOfLastComment);
  const totalCommentPages = Math.ceil(commentList.length / commentsPerPage);

  const handleCommentPageChange = (pageNumber) => {
    setCurrentCommentPage(pageNumber);
  };

  // 찜 목록 state (전체 위시리스트)
  const [wishlistItems, setWishlistItems] = useState(new Set());

  // 1) 마운트/유저 변경 시 위시리스트 불러오기
  useEffect(() => {
    if (!user || !user.email || !user.accessToken) return;
    getWishlistItems(0, 999, {
      email: user.email,
      accessToken: user.accessToken,
    })
        .then((res) => {
          const itemIds = res.wishlist?.map((w) => w.pdtId) || [];
          setWishlistItems(new Set(itemIds));
        })
        .catch((err) => {
          console.error('위시리스트 로딩 오류:', err);
        });
  }, [user]);

  // 3) 찜하기/찜취소 버튼 로직 (다른 사용자가 등록한 상품에 대해서만 보임)
  const handleAddToWishlist = async (item) => {
    if (!user) {
      alert('로그인이 필요한 서비스입니다.');
      navigate('/login');
      return;
    }
    const email = user.email;
    try {
      if (wishlistItems.has(item.pdtId)) {
        // 찜 취소
        await toggleWishdel(email, item.pdtId);
        setWishlistItems((prev) => {
          const newSet = new Set(prev);
          newSet.delete(item.pdtId);
          return newSet;
        });
        alert('위시리스트에서 제거되었습니다!');
      } else {
        // 찜 등록
        await toggleWish(email, item.pdtId, item.pdtName, item.pdtPrice);
        setWishlistItems((prev) => {
          const newSet = new Set(prev);
          newSet.add(item.pdtId);
          return newSet;
        });
        alert('위시리스트에 추가되었습니다!');
      }
    } catch (error) {
      console.error('위시리스트 처리 중 오류 발생:', error);
      alert('위시리스트 처리 중 오류가 발생했습니다.');
    }
  };

  // 페이지 초기화 시 데이터 로드
  useEffect(() => {
    const navigationEntries = performance.getEntriesByType("navigation");
    const isBack = navigationEntries.length > 0 &&
        navigationEntries[0].type === "back_forward";
    setIsBackNavigation(isBack);
    fetchPostData();
  }, [id]);

  const fetchPostData = async () => {
    try {
      const response = await postsAPI.getPostDetail(id, isBackNavigation);
      console.log("response: " + JSON.stringify(response.data, null, 2));
      setItem(response.data);
    } catch (error) {
      console.error('Error fetching post:', error);
      alert('게시글을 불러오는데 실패했습니다.');
    }
  };

  const handleCreateOrJoinChat = async () => {
    if (!user || !user.email) {
      alert("로그인이 필요합니다.");
      return;
    }
    if (!item || !item.email) {
      alert("판매자 정보를 불러올 수 없습니다.");
      return;
    }
    const sellerEmail = item.email; // 상품 등록한 판매자 이메일

    try {
      console.log("🔎 기존 채팅방 확인 요청...");
      const response = await axios.get("http://56.155.23.170:8080/room/list", {
        headers: { "X-Auth-User": user.email }
      });
      const existingRoom = response.data.find(room => {
        const userList = room.users || room.members || room.participants || [];
        return userList.includes(user.email) && userList.includes(sellerEmail);
      });
      if (existingRoom) {
        navigate(`/chat?roomUUID=${existingRoom.roomUUID}`);
        return;
      }
      console.log("🚀 기존 채팅방 없음 → 새로운 채팅방 생성 요청");
      const createResponse = await axios.post(
          `http://56.155.23.170:8080/room/create`,
          {},
          {
            headers: {
              "X-Auth-User": user.email,
              "Content-Type": "application/json"
            },
            params: { user: sellerEmail, name: item.pdtName }
          }
      );
      let newRoomUUID = null;
      if (Array.isArray(createResponse.data) && createResponse.data.length > 0) {
        newRoomUUID = createResponse.data[0]?.roomUUID || createResponse.data[0]?.room?.roomUUID;
      } else if (createResponse.data?.roomUUID) {
        newRoomUUID = createResponse.data.roomUUID;
      } else if (createResponse.data?.room) {
        newRoomUUID = createResponse.data.room.roomUUID;
      }
      if (!newRoomUUID) {
        console.warn("⚠ 채팅방 생성되었으나 UUID를 찾을 수 없음.");
        alert("채팅방을 생성하는데 실패했습니다.");
        return;
      }
      navigate(`/chat?roomUUID=${newRoomUUID}`);
    } catch (error) {
      console.error("❌ 채팅방 생성 또는 조회 중 오류 발생:", error);
      if (error.response) {
        console.error("❌ 서버 응답 데이터:", error.response.data);
      }
      alert("채팅방을 불러오는 중 오류가 발생했습니다.");
    }
  };

  if (!item) return <div>로딩 중...</div>;

  return (
      <Container className="py-5" style={{ display: 'flex', flexDirection: 'column' }}>
        <Row className="d-flex flex-row flex-nowrap justify-content-between align-items-start">
          <Col md={6}>
            <Card className="item-card">
              <Card.Body>
                <div className="item-images">
                  {item.imageUrls && item.imageUrls.length > 0 ? (
                      item.imageUrls.map((filePath, index) => (
                          <div key={index} className="mb-3">
                            <img
                                src={filePath}
                                alt={`첨부 이미지 ${index + 1}`}
                                className="img-fluid"
                            />
                          </div>
                      ))
                  ) : null}
                </div>
              </Card.Body>
            </Card>
          </Col>
          <Col md={6}>
            <Card className="item-card">
              <Card.Body>
                <h4>{item.pdtName}</h4>
                <h3 className="item-price">\{item.pdtPrice}</h3>
                <p>{item.description}</p>
                <div className="divider"></div>
                <div className="like-section">
                  {/* 상품 등록자가 아닌 경우에만 찜하기 버튼 표시 */}
                  {!(user && item.email === user.email) && (
                      <Button className="btn-add-to-cart" onClick={() => handleAddToWishlist(item)}>
                        <FaHeart />
                        {wishlistItems.has(item.pdtId) ? ' 찜취소' : ' 찜해두기'}
                      </Button>
                  )}
                </div>
                <div className="action-buttons mt-3">
                  <div>
                    {/* 상품 등록자가 아닌 경우에만 채팅하기 버튼 표시 */}
                    {!(user && item.email === user.email) && (
                        <Button
                            variant="primary"
                            className="me-2"
                            onClick={() => handleCreateOrJoinChat(item.sellerEmail)}
                        >
                          채팅하기
                        </Button>
                    )}
                  </div>
                  <div>
                    {/* 상품 등록자이면 수정/삭제 버튼 표시 */}
                    {user && item && hasDeletePermission(user, item) && (
                        <>
                          <Button
                              variant="primary"
                              className="me-2"
                              onClick={() => navigate(`/items/edit/${item.pdtId}`)}
                          >
                            수정하기
                          </Button>
                          <Button
                              variant="primary"
                              className="me-2"
                              onClick={async () => {
                                const confirmDelete = window.confirm("정말로 상품을 삭제하시겠습니까?");
                                if (!confirmDelete) return;
                                try {
                                  await postsAPI.deleteItem(item.pdtId, user);
                                  alert("상품이 삭제되었습니다.");
                                  navigate(-1);
                                } catch (error) {
                                  alert("상품 삭제에 실패했습니다.");
                                  console.error(error);
                                }
                              }}
                          >
                            상품삭제
                          </Button>
                        </>
                    )}
                  </div>
                </div>
                <div className="action-buttons delivery mt-3">
                  <Button variant="success">배달 \3000</Button>
                  <Button variant="success">직거래</Button>
                </div>
                <div className="item-meta">
                  <span className="item-date">2020.2.2.</span>
                  <span className="item-views">조회수: 3</span>
                </div>
              </Card.Body>
            </Card>
          </Col>
        </Row>
      </Container>
  );
}

export default ItemDetail;
