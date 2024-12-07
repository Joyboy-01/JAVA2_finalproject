package CS209A.project.demo.service;

import CS209A.project.demo.entity.Comment;
import CS209A.project.demo.repository.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import CS209A.project.demo.entity.User;


@Service
public class CommentService {
    @Autowired
    private CommentRepository commentRepository;
//    public CommentService(CommentRepository repo) {
//        this.repo = repo;
//    }
//    public List<Comment> findAll(){return repo.findAll();}
//    public Comment save(Comment c){return repo.save(c);}
//    public Comment findById(Long id){return repo.findById(id).orElse(null);}
//    public void delete(Long id){repo.deleteById(id);}


    public List<Comment> getHighReputationComments(List<User> highReputationUsers) {
        // 将高声誉用户的 userId 提取到一个 Set 中
        Set<Long> highReputationUserIds = highReputationUsers.stream()
                .map(User::getExternalUserId)  // 提取每个高声誉用户的 userId
                .collect(Collectors.toSet());  // 将所有 userId 收集到一个 Set 中

        // 从数据库中获取所有评论，并过滤出属于高声誉用户的评论
        return commentRepository.findAll().stream()
                .filter(comment -> highReputationUserIds.contains(comment.getUserId()))  // 过滤：如果评论的用户在高声誉用户中，则保留
                .collect(Collectors.toList());  // 将符合条件的评论收集到一个 List 中返回
    }

}