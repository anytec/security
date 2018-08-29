package cn.anytec.security.component.ipcamera;

//@Component
public class IPCMonitor {

//    Logger logger = LoggerFactory.getLogger(IPCMonitor.class);
//
//    @Value("${ipc.port}")
//    private int ipcPort;
//
//    //每次发送接收的数据包大小
//    private final int MAX_BUFF_SIZE = 1024 * 1;
//    //服务端监听端口，客户端也通过该端口发送数据
//    private int port;
//    private DatagramChannel channel;
//    private Selector selector;
//
//    private ScheduledExecutorService es = Executors.newScheduledThreadPool(1);
//
////    // 获取通道
////    DatagramChannel datagramChannel;
////    // 分配Buffer
////    ByteBuffer buffer = ByteBuffer.allocate(1024);
//
//    @PostConstruct
//    public void init() throws IOException {
//        logger.info("begin to init ipc camera connection channel");
//        //创建通道和选择器
//        selector = Selector.open();
//        channel = DatagramChannel.open();
//        //设置为非阻塞模式
//        channel.configureBlocking(false);
//        channel.socket().bind(new InetSocketAddress(port));
//        //将通道注册至selector，监听只读消息（此时服务端只能读数据，无法写数据）
//        channel.register(selector, SelectionKey.OP_READ);
//
//        //使用线程的方式，保证服务端持续等待接收客户端数据
//        es.scheduleWithFixedDelay(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    logger.info("start thread for ipcamera connection");
//                    while(selector.select() > 0) {
//                        Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
//                        while(iterator.hasNext()) {
//                            SelectionKey key = iterator.next();
//                            try {
//                                iterator.remove();
//                                if(key.isReadable()) {
//                                    //接收数据
//                                    logger.info("receive ipc connect");
//                                    doReceive(key);
//                                }
//                            } catch (Exception e) {
//                                logger.error("SelectionKey receive exception", e);
//                                try {
//                                    if (key != null) {
//                                        key.cancel();
//                                        key.channel().close();
//                                    }
//                                } catch (ClosedChannelException cex) {
//                                    logger.error("Close channel exception", cex);
//                                }
//                            }
//                        }
//                    }
//                } catch (IOException e) {
//                    logger.error("selector.select exception", e);
//                }
//            }
//        }, 0L, 2L, TimeUnit.MINUTES);
//
//    }
//    //处理接收到的数据
//    private void doReceive(SelectionKey key) throws IOException {
//        String content = "";
//        DatagramChannel sc = (DatagramChannel) key.channel();
//        ByteBuffer buffer = ByteBuffer.allocate(MAX_BUFF_SIZE);
//        buffer.clear();
//        SocketAddress socketAddress = sc.receive(buffer);
//        buffer.flip();
//        while(buffer.hasRemaining()) {
//            byte[] buf = new byte[buffer.limit()];
//            buffer.get(buf);
//            content += new String(buf);
//        }
//        buffer.clear();
//        logger.debug("receive content="+content);
//        if(StringUtils.isNotBlank(content)) {
//            JSONObject jsonObject = JSONObject.parseObject(content);
//            logger.info(jsonObject.toJSONString());
//            ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
//            buffer.put("accepted".getBytes("UTF-8"));
//            buffer.flip();
//            sc.send(buffer,socketAddress);
//        }
//    }


}
